package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import kahoot.clabs.application.command.HostActionCommand;
import kahoot.clabs.application.command.OpenQuestionCommand;
import kahoot.clabs.application.dto.GameSessionResponse;
import kahoot.clabs.application.event.GameSessionIntegrationEvent;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot;
import kahoot.clabs.application.port.integration.GameSessionEventPublisher;
import kahoot.clabs.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.repository.GameSessionRepository;

@ApplicationScoped
public class ManageSessionQuestionsUseCase {

    @Inject
    GameSessionRepository gameSessionRepository;

    @Inject
    OrganizationMembershipPort organizationMembershipPort;

    @Inject
    GameSessionEventPublisher gameSessionEventPublisher;

    @Transactional
    public GameSessionResponse open(UUID organizationId, UUID sessionId, OpenQuestionCommand command) {
        GameSession session = loadForHost(organizationId, sessionId, command.hostUserId());
        session.openQuestion(command.questionIndex());
        GameSession saved = gameSessionRepository.save(session);
        gameSessionEventPublisher.publish(
                GameSessionIntegrationEvent.questionOpened(GameSessionProjectionSnapshot.from(saved)));
        return GameSessionResponse.from(saved);
    }

    @Transactional
    public GameSessionResponse close(UUID organizationId, UUID sessionId, HostActionCommand command) {
        GameSession session = loadForHost(organizationId, sessionId, command.hostUserId());
        session.closeQuestion();
        GameSession saved = gameSessionRepository.save(session);
        gameSessionEventPublisher.publish(
                GameSessionIntegrationEvent.questionClosed(GameSessionProjectionSnapshot.from(saved)));
        return GameSessionResponse.from(saved);
    }

    @Transactional
    public GameSessionResponse next(UUID organizationId, UUID sessionId, HostActionCommand command) {
        GameSession session = loadForHost(organizationId, sessionId, command.hostUserId());
        session.nextQuestion();
        GameSession saved = gameSessionRepository.save(session);
        gameSessionEventPublisher.publish(
                GameSessionIntegrationEvent.questionAdvanced(GameSessionProjectionSnapshot.from(saved)));
        return GameSessionResponse.from(saved);
    }

    private GameSession loadForHost(UUID organizationId, UUID sessionId, UUID hostUserId) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, hostUserId);
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.ensureHost(hostUserId);
        return session;
    }
}
