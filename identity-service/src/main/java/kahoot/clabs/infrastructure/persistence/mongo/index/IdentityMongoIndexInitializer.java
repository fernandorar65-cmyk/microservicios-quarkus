package kahoot.clabs.infrastructure.persistence.mongo.index;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class IdentityMongoIndexInitializer {

    private final MongoClient mongoClient;
    private final String databaseName;

    @Inject
    public IdentityMongoIndexInitializer(
            MongoClient mongoClient,
            @ConfigProperty(name = "quarkus.mongodb.database") String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    void onStart(@Observes StartupEvent event) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoIndexSupport.ensureCollections(
                database, "permissions", "roles", "role_permissions", "users", "user_images");

        var users = database.getCollection("users");
        MongoIndexSupport.ensureIndex(
                users, Indexes.ascending("email"), new IndexOptions().unique(true).name("users_email_uq"));
        MongoIndexSupport.ensureIndex(users, Indexes.ascending("status"), new IndexOptions().name("users_status_idx"));
        MongoIndexSupport.ensureIndex(
                users, Indexes.ascending("role.type"), new IndexOptions().name("users_role_type_idx"));

        var roles = database.getCollection("roles");
        MongoIndexSupport.ensureIndex(
                roles, Indexes.ascending("type"), new IndexOptions().unique(true).name("roles_type_uq"));

        var permissions = database.getCollection("permissions");
        MongoIndexSupport.ensureIndex(
                permissions,
                Indexes.ascending("name", "module"),
                new IndexOptions().unique(true).name("permissions_name_module_uq"));

        var rolePermissions = database.getCollection("role_permissions");
        MongoIndexSupport.ensureIndex(
                rolePermissions, Indexes.ascending("roleId"), new IndexOptions().name("role_permissions_role_idx"));
        MongoIndexSupport.ensureIndex(
                rolePermissions,
                Indexes.ascending("permissionId"),
                new IndexOptions().name("role_permissions_permission_idx"));

        var userImages = database.getCollection("user_images");
        MongoIndexSupport.ensureIndex(
                userImages, Indexes.ascending("userId"), new IndexOptions().name("user_images_user_idx"));
        MongoIndexSupport.ensureIndex(
                userImages,
                Indexes.ascending("userId", "type"),
                new IndexOptions().name("user_images_user_type_idx"));
    }
}
