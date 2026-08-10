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
        MongoIndexSupport.ensureIndex(
                collection, Indexes.ascending("organizationId"), new IndexOptions().name("sessions_org_idx"));
        MongoIndexSupport.ensureIndex(
                collection, Indexes.ascending("quizId"), new IndexOptions().name("sessions_quiz_idx"));
        MongoIndexSupport.ensureIndex(
                collection, Indexes.ascending("hostUserId"), new IndexOptions().name("sessions_host_idx"));
        MongoIndexSupport.ensureIndex(
                collection, Indexes.ascending("status"), new IndexOptions().name("sessions_status_idx"));
        MongoIndexSupport.ensureIndex(
                collection,
                Indexes.ascending("players.userId"),
                new IndexOptions().name("sessions_player_user_idx"));
    }

    private void initializeLeaderboardIndexes(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("leaderboards");
        MongoIndexSupport.ensureIndex(
                collection,
                Indexes.ascending("sessionId"),
                new IndexOptions().unique(true).name("leaderboards_session_uq"));
    }

    private void initializePlayableQuizSnapshotIndexes(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("playable_quiz_snapshots");
        MongoIndexSupport.ensureIndex(
                collection,
                Indexes.ascending("organizationId"),
                new IndexOptions().name("playable_quiz_org_idx"));
    }
}
