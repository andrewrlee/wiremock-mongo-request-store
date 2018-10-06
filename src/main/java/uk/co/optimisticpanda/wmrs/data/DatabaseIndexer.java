package uk.co.optimisticpanda.wmrs.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;

import static com.google.common.collect.Multimaps.synchronizedMultimap;

public class DatabaseIndexer {

    private static final Logger L = LoggerFactory.getLogger(DatabaseIndexer.class);

    private final Multimap<String, String> collectionToKnownIndexes = synchronizedMultimap(HashMultimap.create());

    public void ensureDefaultFieldsIndexed(MongoCollection<StoredEntry> collection) {

        indexIfNeccessary(collection, StoredEntry.ID);
        indexIfNeccessary(collection, StoredEntry.TIMESTAMP);
    }


    public void ensureFieldsIndexed(MongoCollection<StoredEntry> collection, Set<String> fields) {

        fields.forEach(field -> indexIfNeccessary(collection, field));
    }


    private void indexIfNeccessary(MongoCollection<StoredEntry> collection, String fieldName) {

        if (isKnownIndex(collection, fieldName)) {
            return;
        }

        collection.listIndexes().into(new ArrayList<>(), (indexes, t) -> {

            handleError(collection, "Failed to list indexes", t);

            if (t != null) {
                L.error("error querying index on collection: {}", collection.getNamespace(), t);
                return;
            }

            indexes.stream().map(d -> d.getString("name")).forEach(indexName ->
                    collectionToKnownIndexes.put(collection.getNamespace().getFullName(), indexName));

            if (isKnownIndex(collection, fieldName)) {
                return;
            }

            IndexOptions indexOptions = new IndexOptions()
                    .name(fieldName)
                    .background(true);

            collection.createIndex(Indexes.ascending(fieldName), indexOptions, (indexName, tx) -> {
                handleError(collection, "Failed to create index " + fieldName, tx);
                L.info("Created index: {}/{}", collection.getNamespace().getFullName(), indexName);
                collectionToKnownIndexes.put(collection.getNamespace().getFullName(), indexName);
            });
        });
    }

    private void handleError(final MongoCollection<StoredEntry> collection, final String message, final Throwable tx) {
        if (tx != null) {
            String completeMessage =  message + ", on collection: " + collection.getNamespace();
            L.error(completeMessage, tx);
            throw new RuntimeException(completeMessage, tx);
        }
    }

    private boolean isKnownIndex(MongoCollection<StoredEntry> collection, String fieldName) {

        boolean containsEntry = collectionToKnownIndexes.containsEntry(collection.getNamespace().getFullName(), fieldName);

        if (containsEntry) {
            L.debug("Field: {} already indexed, returning...", fieldName);
        }

        return containsEntry;
    }
}
