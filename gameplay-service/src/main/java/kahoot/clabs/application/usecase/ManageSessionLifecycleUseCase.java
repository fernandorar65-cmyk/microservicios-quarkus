package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import kahoot.clabs.application.command.HostActionCommand;
import kahoot.clabs.application.dto.GameSessionResponse;
import kahoot.clabs.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.application.port.integration.QuizSnapshotPort;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.repository.GameSessionRepository;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class ManageSessionLifecycleUseCase {

    @Inject
    GameSessionRepository gameSessionRepository;

    @Inject
    OrganizationMembershipPort organizationMembershipPort;

    @Inject
    QuizSnapshotPort quizSnapshotPort;

    @Transactional
    public GameSessionResponse start(UUID organizationId, UUID sessionId, HostActionCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.hostUserId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.ensureHost(command.hostUserId());

        if (session.getQuestions().isEmpty()) {
            PublishedQuizSnapshot snapshot = quizSnapshotPort
                    .findPublishedByOrganizationAndId(organizationId, session.getQuizId())
                    .orElseThrow(() -> new DomainException(
                            "Published quiz not found for organization: " + session.getQuizId()));
            GameSessionSupport.freezeFromSnapshot(session, snapshot);
        }
        session.start();
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public GameSessionResponse cancel(UUID organizationId, UUID sessionId, HostActionCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.hostUserId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.ensureHost(command.hostUserId());
        session.cancel();
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public GameSessionResponse finish(UUID organizationId, UUID sessionId, HostActionCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.hostUserId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.ensureHost(command.hostUserId());
        session.finish();
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }
}
