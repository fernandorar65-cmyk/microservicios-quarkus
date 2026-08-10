package kahoot.clabs.infrastructure.persistence.mongo.index;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;

final class MongoIndexSupport {

    private static final Logger LOG = Logger.getLogger(MongoIndexSupport.class);

    private MongoIndexSupport() {
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
