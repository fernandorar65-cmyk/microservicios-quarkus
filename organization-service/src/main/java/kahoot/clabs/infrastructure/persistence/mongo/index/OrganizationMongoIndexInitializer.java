package kahoot.clabs.infrastructure.persistence.mongo.index;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.bson.Document;

@ApplicationScoped
public class OrganizationMongoIndexInitializer {

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;

    void onStart(@Observes StartupEvent event) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);

        MongoIndexSupport.ensureCollections(
                mongoDatabase,
                "organizations",
                "organization_members",
                "organization_departments",
                "organization_jobs",
                "organization_statuses",
                "organization_member_statuses");

        MongoCollection<Document> organizations = mongoDatabase.getCollection("organizations");
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

        MongoCollection<Document> members = mongoDatabase.getCollection("organization_members");
        MongoIndexSupport.ensureIndex(
                members, Indexes.ascending("userId"), new IndexOptions().name("organization_members_user_idx"));
        MongoIndexSupport.ensureIndex(
                members,
                Indexes.ascending("organizationId", "userId"),
                new IndexOptions().unique(true).name("organization_members_org_user_uq"));

        MongoIndexSupport.ensureIndex(
                mongoDatabase.getCollection("organization_departments"),
                Indexes.ascending("name"),
                new IndexOptions().unique(true).name("organization_departments_name_uq"));
        MongoIndexSupport.ensureIndex(
                mongoDatabase.getCollection("organization_jobs"),
                Indexes.ascending("name"),
                new IndexOptions().unique(true).name("organization_jobs_name_uq"));
        MongoIndexSupport.ensureIndex(
                mongoDatabase.getCollection("organization_statuses"),
                Indexes.ascending("name"),
                new IndexOptions().unique(true).name("organization_statuses_name_uq"));
        MongoIndexSupport.ensureIndex(
                mongoDatabase.getCollection("organization_member_statuses"),
                Indexes.ascending("name"),
                new IndexOptions().unique(true).name("organization_member_statuses_name_uq"));
    }
}
