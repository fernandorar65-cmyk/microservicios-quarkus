package kahoot.clabs.infrastructure.persistence.mongo.index;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import org.bson.Document;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GameplayMongoIndexInitializer {

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String databaseName;

    void onStart(@Observes StartupEvent event) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        initializeGameSessionIndexes(database);
        initializeLeaderboardIndexes(database);
        initializePlayableQuizSnapshotIndexes(database);
    }

    private void initializeGameSessionIndexes(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("game_sessions");

        collection.createIndex(Indexes.ascending("organizationId"));
        collection.createIndex(Indexes.ascending("quizId"));
        collection.createIndex(Indexes.ascending("hostUserId"));
        collection.createIndex(Indexes.ascending("status"));
        collection.createIndex(Indexes.ascending("players.userId"));
    }

    private void initializeLeaderboardIndexes(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("leaderboards");

        collection.createIndex(
                Indexes.ascending("sessionId"),
                new IndexOptions().unique(true));
    }

    private void initializePlayableQuizSnapshotIndexes(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("playable_quiz_snapshots");
        collection.createIndex(Indexes.ascending("organizationId"));
    }
}
