package kahoot.clabs.application.port.integration;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;

public interface QuizSnapshotPort {

    Optional<PublishedQuizSnapshot> findPublishedByOrganizationAndId(UUID organizationId, UUID quizId);
}
