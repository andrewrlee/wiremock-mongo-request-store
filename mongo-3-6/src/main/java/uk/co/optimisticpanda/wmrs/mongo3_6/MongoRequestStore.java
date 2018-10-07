package uk.co.optimisticpanda.wmrs.mongo3_6;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.mongodb.ConnectionString;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.optimisticpanda.wmrs.core.Entry;
import uk.co.optimisticpanda.wmrs.core.EntryQuery;
import uk.co.optimisticpanda.wmrs.core.EntryToStore;
import uk.co.optimisticpanda.wmrs.core.ListQuery;
import uk.co.optimisticpanda.wmrs.core.RequestStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.core.JsonParser.Feature.IGNORE_UNDEFINED;
import static com.google.common.base.Preconditions.checkState;
import static com.mongodb.async.client.MongoClients.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;
import static java.time.ZoneOffset.UTC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static uk.co.optimisticpanda.wmrs.mongo3_6.StoredEntry.TAGS;
import static uk.co.optimisticpanda.wmrs.mongo3_6.StoredEntry.TIMESTAMP;

public class MongoRequestStore implements RequestStore {

    private static final Logger L = LoggerFactory.getLogger(MongoRequestStore.class);

    private final MongoDatabase database;
    private final CodecRegistry codecRegistry;
    private final DatabaseIndexer databaseIndexer = new DatabaseIndexer();
    private final Map<String, MongoCollection<StoredEntry>> collections = new ConcurrentHashMap<>();

    public MongoRequestStore(final String connectionString, final String database) {

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

    @Override
    public void save(final String storeName, final EntryToStore entry) {

        MongoCollection<StoredEntry> collection = collectionFor(storeName);

        StoredEntry storedEntry = new StoredEntry(
                entry.getId(),
                entry.getTimestamp(),
                entry.getRequest(),
                entry.getResponse(),
                entry.getTags(),
                entry.getFields());

        collection.insertOne(storedEntry, (result, ex) -> {
            if (ex != null) {
                L.error("Failed to insert entry: {}", entry, ex);
            }
            databaseIndexer.ensureFieldsIndexed(collection, entry.getTags());
        });
    }

    @Override
    public Entry query(final EntryQuery query) {

        checkCollectionExists(query.getStoreName());

        return syncQuery(
                collectionFor(query.getStoreName())
                        .find(eq("id", query.getId())))
                .iterator().next();
    }

    @Override
    public List<? extends Entry> query(final ListQuery query) {

        checkCollectionExists(query.getStoreName());

        Collection<Bson> queries = new ArrayList<>();

        query.getTag().ifPresent(tag -> queries.add(in(TAGS, tag)));
        query.getSince().ifPresent(since-> queries.add(gte(TIMESTAMP, Date.from(since.toInstant(UTC)))));
        query.getFieldsToMatch().forEach((field, value) -> queries.add(eq("fields." + field, value)));

        MongoCollection<StoredEntry> collection = collectionFor(query.getStoreName());

        return syncQuery(
                collection
                        .find(queries.isEmpty() ? new Document() : and(queries))
                        .sort(descending(TIMESTAMP))
                        .skip(query.getOffset().orElse(0))
                        .projection(include("id", "timestamp", "tags"))
                        .limit(query.getLimit().orElse(12)));
    }

    private void checkCollectionExists(final String storeName) {
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

    private MongoCollection<StoredEntry> collectionFor(final String storeName) {
        return collections.computeIfAbsent(storeName, this::createCollection);
    }

    private MongoCollection<StoredEntry> createCollection(final String collectionName) {

        MongoCollection<StoredEntry> collection = database
                .getCollection(collectionName, StoredEntry.class)
                .withCodecRegistry(codecRegistry)
                .withDocumentClass(StoredEntry.class);

        databaseIndexer.ensureDefaultFieldsIndexed(collection);

        return collection;
    }

    private List<StoredEntry> syncQuery(final FindIterable<StoredEntry> iterable) {

        CompletableFuture<List<StoredEntry>> result = new CompletableFuture<>();
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
