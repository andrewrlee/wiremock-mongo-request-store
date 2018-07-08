package uk.co.optimisticpanda.wmrs.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.mongodb.ConnectionString;
import com.mongodb.async.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.optimisticpanda.wmrs.core.RequestQuery;
import uk.co.optimisticpanda.wmrs.core.RequestStore;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.JsonParser.Feature.*;
import static com.google.common.base.Preconditions.checkState;
import static com.mongodb.async.client.MongoClients.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;
import static java.time.ZoneOffset.UTC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static uk.co.optimisticpanda.wmrs.data.Entry.TAGS;
import static uk.co.optimisticpanda.wmrs.data.Entry.TIMESTAMP;

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
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDateFormat(new ISO8601DateFormat())
                .configure(IGNORE_UNDEFINED, true);

        this.codecRegistry = fromRegistries(
                getDefaultCodecRegistry(),
                fromProviders(new JacksonCodecProvider(objectMapper)));

        MongoClient client = MongoClients.create(new ConnectionString(connectionString));

        this.database = client.getDatabase(database);
    }

    public void save(String storeName, Entry entry) {

        collectionFor(storeName).insertOne(entry, (result, ex) -> {
            if (ex != null) {
                L.error("Failed to insert entry: {}", entry, ex);
            }
        });
    }

    private MongoCollection<Entry> collectionFor(String storeName) {
        return collections.computeIfAbsent(storeName, this::createCollection);
    }

    @Override
    public List<Entry> query(RequestQuery query) {

        checkCollectionExists(query.getStoreName());

        Collection<Bson> queries = new ArrayList<>();

        query.getTag().ifPresent(tag -> queries.add(in(TAGS, tag)));
        query.getSince().ifPresent(since-> queries.add(gte(TIMESTAMP, Date.from(since.toInstant(UTC)))));
        query.getFieldsToMatch().forEach((field, value) -> queries.add(eq("fields." + field, value)));

        return syncQuery(
                collectionFor(query.getStoreName())
                        .find(queries.isEmpty() ? new Document() : Filters.and(queries))
                        .sort(descending(TIMESTAMP))
                        .limit(query.getLimit().orElse(0)));
    }

    private void checkCollectionExists(String storeName) {
        CompletableFuture<Set<String>> result = new CompletableFuture<>();
        try {

            database.listCollectionNames().into(new HashSet<>(), (names, ex) -> {
                if (ex != null) {
                    throw new RuntimeException("error retrieving result: " + names, ex);
                }
                result.complete(names);
            });

            Set<String> availableStores = result.get(10L, SECONDS);
            checkState(
                    availableStores.contains(storeName),
                    "store: '%s' not present!, available: '%s'", storeName, availableStores);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("error retrieving collection names: " + storeName, e);
        }
    }

    private MongoCollection<Entry> createCollection(final String collectionName) {
        return database
                .getCollection(collectionName, Entry.class)
                .withCodecRegistry(codecRegistry)
                .withDocumentClass(Entry.class);
    }

    private List<Entry> syncQuery(FindIterable<Entry> iterable) {

        CompletableFuture<List<Entry>> result = new CompletableFuture<>();
        try {

            iterable.into(new ArrayList<>(), (entries, ex) -> {
                if (ex != null) {
                    throw new RuntimeException("error retrieving result: " + iterable, ex);
                }
                result.complete(entries);
            });

            return result.get(10L, SECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("error retrieving result: " + iterable, e);
        }
    }
}
