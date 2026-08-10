package kahoot.clabs.quiz.infrastructure.persistence.mongo.index;

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
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoIndexSupport.ensureCollections(
                database,
                "quizzes",
                "categories",
                "quiz_categories",
                "questions",
                "answer_options",
                "question_assets");

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

        var quizCategories = database.getCollection("quiz_categories");
        MongoIndexSupport.ensureIndex(
                quizCategories, Indexes.ascending("quizId"), new IndexOptions().name("quiz_categories_quiz_idx"));
        MongoIndexSupport.ensureIndex(
                quizCategories,
                Indexes.ascending("categoryId"),
                new IndexOptions().name("quiz_categories_category_idx"));

        var questions = database.getCollection("questions");
        MongoIndexSupport.ensureIndex(
                questions, Indexes.ascending("quizId"), new IndexOptions().name("questions_quiz_idx"));
        MongoIndexSupport.ensureIndex(
                questions,
                Indexes.ascending("quizId", "orderIndex"),
                new IndexOptions().unique(true).name("questions_quiz_order_uq"));

        var answerOptions = database.getCollection("answer_options");
        MongoIndexSupport.ensureIndex(
                answerOptions, Indexes.ascending("questionId"), new IndexOptions().name("answer_options_question_idx"));
        MongoIndexSupport.ensureIndex(
                answerOptions,
                Indexes.ascending("questionId", "orderIndex"),
                new IndexOptions().unique(true).name("answer_options_question_order_uq"));

        var questionAssets = database.getCollection("question_assets");
        MongoIndexSupport.ensureIndex(
                questionAssets,
                Indexes.ascending("questionId"),
                new IndexOptions().unique(true).name("question_assets_question_uq"));
    }
}
