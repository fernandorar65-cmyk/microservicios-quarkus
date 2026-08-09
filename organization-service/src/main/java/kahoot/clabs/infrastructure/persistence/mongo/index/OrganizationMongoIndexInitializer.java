package kahoot.clabs.infrastructure.persistence.mongo.index;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.bson.Document;

@ApplicationScoped
public class OrganizationMongoIndexInitializer {

    private static final String ORGANIZATIONS_COLLECTION = "organizations";

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;

    void onStart(@Observes StartupEvent event) {
        MongoCollection<Document> organizations =
                mongoClient.getDatabase(database).getCollection(ORGANIZATIONS_COLLECTION);

        organizations.createIndex(Indexes.ascending("slug"), new IndexOptions().unique(true));
        organizations.createIndex(Indexes.ascending("status"));
        organizations.createIndex(Indexes.ascending("members.userId"));
    }
}
