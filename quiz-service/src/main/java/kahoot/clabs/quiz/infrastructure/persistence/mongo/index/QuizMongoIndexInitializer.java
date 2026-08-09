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
        quizzes.createIndex(Indexes.ascending("organizationId"));
        quizzes.createIndex(Indexes.ascending("createdBy"));
        quizzes.createIndex(Indexes.ascending("status"));
        quizzes.createIndex(Indexes.ascending("difficulty"));
        quizzes.createIndex(Indexes.ascending("categories.id"));
        quizzes.createIndex(Indexes.ascending("title"));

        var categories = database.getCollection("categories");
        categories.createIndex(
                Indexes.ascending("organizationId", "name"),
                new IndexOptions().unique(true));
        categories.createIndex(Indexes.ascending("organizationId"));
    }
}
