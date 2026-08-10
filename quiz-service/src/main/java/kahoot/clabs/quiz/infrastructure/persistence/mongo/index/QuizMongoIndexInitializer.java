package kahoot.clabs.quiz.infrastructure.persistence.mongo.index;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class QuizMongoIndexInitializer {

    private final MongoClient mongoClient;
    private final String databaseName;

    @Inject
    public QuizMongoIndexInitializer(
            MongoClient mongoClient,
            @ConfigProperty(name = "quarkus.mongodb.database") String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    void onStart(@Observes StartupEvent event) {
        var database = mongoClient.getDatabase(databaseName);

        var quizzes = database.getCollection("quizzes");
        MongoIndexSupport.ensureIndex(
                quizzes, Indexes.ascending("organizationId"), new IndexOptions().name("quizzes_org_idx"));
        MongoIndexSupport.ensureIndex(
                quizzes, Indexes.ascending("createdBy"), new IndexOptions().name("quizzes_created_by_idx"));
        MongoIndexSupport.ensureIndex(
                quizzes, Indexes.ascending("status"), new IndexOptions().name("quizzes_status_idx"));
        MongoIndexSupport.ensureIndex(
                quizzes, Indexes.ascending("difficulty"), new IndexOptions().name("quizzes_difficulty_idx"));
        MongoIndexSupport.ensureIndex(
                quizzes, Indexes.ascending("categories.id"), new IndexOptions().name("quizzes_category_idx"));
        MongoIndexSupport.ensureIndex(
                quizzes, Indexes.ascending("title"), new IndexOptions().name("quizzes_title_idx"));

        var categories = database.getCollection("categories");
        MongoIndexSupport.ensureIndex(
                categories,
                Indexes.ascending("organizationId", "name"),
                new IndexOptions().unique(true).name("categories_org_name_uq"));
        MongoIndexSupport.ensureIndex(
                categories,
                Indexes.ascending("organizationId"),
                new IndexOptions().name("categories_org_idx"));
    }
}
