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

        MongoIndexSupport.ensureIndex(
                organizations,
                Indexes.ascending("slug"),
                new IndexOptions().unique(true).name("organizations_slug_uq"));
        MongoIndexSupport.ensureIndex(
                organizations, Indexes.ascending("status"), new IndexOptions().name("organizations_status_idx"));
        MongoIndexSupport.ensureIndex(
                organizations,
                Indexes.ascending("members.userId"),
                new IndexOptions().name("organizations_members_user_idx"));
    }
}
