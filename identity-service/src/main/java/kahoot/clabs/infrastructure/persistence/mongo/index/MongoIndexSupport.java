package kahoot.clabs.infrastructure.persistence.mongo.index;

import java.util.HashSet;
import java.util.Set;

import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

final class MongoIndexSupport {

    private static final Logger LOG = Logger.getLogger(MongoIndexSupport.class);

    private MongoIndexSupport() {
    }

    static void ensureCollections(MongoDatabase database, String... collectionNames) {
        Set<String> existing = new HashSet<>();
        database.listCollectionNames().into(existing);
        for (String name : collectionNames) {
            if (!existing.contains(name)) {
                database.createCollection(name);
            }
        }
    }

    static void ensureIndex(MongoCollection<Document> collection, Bson keys, IndexOptions options) {
        try {
            collection.createIndex(keys, options != null ? options : new IndexOptions());
        } catch (MongoCommandException ex) {

            if (ex.getErrorCode() == 85 || ex.getErrorCode() == 86) {
                LOG.warnf(
                        "Skipping Mongo index on %s (already exists): %s",
                        collection.getNamespace().getCollectionName(),
                        ex.getErrorMessage());
                return;
            }
            throw ex;
        }
    }

    static void ensureIndex(MongoCollection<Document> collection, Bson keys) {
        ensureIndex(collection, keys, null);
    }
}
