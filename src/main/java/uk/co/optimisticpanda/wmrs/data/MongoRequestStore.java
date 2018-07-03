package uk.co.optimisticpanda.wmrs.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.optimisticpanda.wmrs.Entry;
import uk.co.optimisticpanda.wmrs.RequestStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.JsonParser.Feature.*;
import static com.mongodb.async.client.MongoClients.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoRequestStore implements RequestStore {

    private static final Logger L = LoggerFactory.getLogger(MongoRequestStore.class);

    private final MongoDatabase database;
    private final Map<String, MongoCollection<Entry>> collections = new ConcurrentHashMap<>();
    private final CodecRegistry codecRegistry;

    public MongoRequestStore(String connectionString, String database) {

        ObjectMapper objectMapper = new ObjectMapper()
                .setSerializationInclusion(NON_NULL)
                .configure(ALLOW_COMMENTS, true)
                .configure(ALLOW_SINGLE_QUOTES, true)
                .configure(IGNORE_UNDEFINED, true);

        this.codecRegistry = fromRegistries(
                getDefaultCodecRegistry(),
                fromProviders(new JacksonCodecProvider(objectMapper)));

        MongoClient client = MongoClients.create(new ConnectionString(connectionString));

        this.database = client.getDatabase(database);
    }

    public void save(String storeName, Entry entry) {

        MongoCollection<Entry> collection = collections.computeIfAbsent(storeName, this::createCollection);

        collection.insertOne(entry, (result, ex) -> {
            if (ex != null) {
                L.error("Failed to insert entry: {}", entry, ex);
            }
        });
    }


    private MongoCollection<Entry> createCollection(final String collectionName) {
        return database
                .getCollection(collectionName, Entry.class)
                .withCodecRegistry(codecRegistry)
                .withDocumentClass(Entry.class);
    }
}
