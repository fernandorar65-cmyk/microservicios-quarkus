package kahoot.clabs.infrastructure.persistence.mongo.repository;

import java.util.Optional;
import java.util.UUID;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.mongo.document.PlayableQuizSnapshotDocument;

@ApplicationScoped
public class PlayableQuizSnapshotMongoRepository implements PanacheMongoRepositoryBase<PlayableQuizSnapshotDocument, UUID> {

    public Optional<PlayableQuizSnapshotDocument> findByOrganizationAndQuizId(UUID organizationId, UUID quizId) {
        return find("organizationId = ?1 and _id = ?2", organizationId, quizId).firstResultOptional();
    }
}
