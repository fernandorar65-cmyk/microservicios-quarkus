package kahoot.clabs.infrastructure.integration;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.port.out.integration.QuizSnapshotPort;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class QuizSnapshotStubAdapter implements QuizSnapshotPort {

    @Override
    public Optional<PublishedQuizSnapshot> findPublishedByOrganizationAndId(UUID organizationId, UUID quizId) {
        throw new DomainException(
                "Quiz snapshot not available — published quiz projection from Kafka is pending for quiz "
                        + quizId + " in organization " + organizationId);
    }
}
