package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import kahoot.clabs.application.command.CreateGameSessionCommand;
import kahoot.clabs.application.dto.GameSessionResponse;
import kahoot.clabs.application.event.GameSessionIntegrationEvent;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot;
import kahoot.clabs.application.port.integration.GameSessionEventPublisher;
import kahoot.clabs.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.application.port.integration.QuizSnapshotPort;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.repository.GameSessionRepository;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class CreateGameSessionUseCase {

    @Inject
    OrganizationMembershipPort organizationMembershipPort;

    @Inject
    QuizSnapshotPort quizSnapshotPort;

    @Inject
    CreateGameSessionWriter writer;


    public GameSessionResponse execute(UUID organizationId, CreateGameSessionCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.hostUserId());

        PublishedQuizSnapshot snapshot = quizSnapshotPort
                .findPublishedByOrganizationAndId(organizationId, command.quizId())
                .orElseThrow(() -> new DomainException(
                        "Published quiz not found for organization: " + command.quizId()));

        return writer.persist(organizationId, command, snapshot);
    }

    @ApplicationScoped
    static class CreateGameSessionWriter {

        @Inject
        GameSessionRepository gameSessionRepository;

        @Inject
        GameSessionEventPublisher gameSessionEventPublisher;

        @Transactional
        public GameSessionResponse persist(
                UUID organizationId, CreateGameSessionCommand command, PublishedQuizSnapshot snapshot) {
            GameSession session = GameSession.create(organizationId, snapshot.quizId(), command.hostUserId());
            GameSessionSupport.freezeFromSnapshot(session, snapshot);
            GameSession saved = gameSessionRepository.save(session);
            gameSessionEventPublisher.publish(
                    GameSessionIntegrationEvent.sessionCreated(GameSessionProjectionSnapshot.from(saved)));
            return GameSessionResponse.from(saved);
        }
    }
}
