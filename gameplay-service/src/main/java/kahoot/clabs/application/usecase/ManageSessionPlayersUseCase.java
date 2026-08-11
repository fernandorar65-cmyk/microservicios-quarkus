package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import kahoot.clabs.application.command.JoinSessionCommand;
import kahoot.clabs.application.command.LeaveSessionCommand;
import kahoot.clabs.application.command.UpdateNicknameCommand;
import kahoot.clabs.application.dto.GameSessionResponse;
import kahoot.clabs.application.dto.SessionPlayerResponse;
import kahoot.clabs.application.event.GameSessionIntegrationEvent;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot;
import kahoot.clabs.application.port.integration.GameSessionEventPublisher;
import kahoot.clabs.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.repository.GameSessionRepository;

@ApplicationScoped
public class ManageSessionPlayersUseCase {

    @Inject
    GameSessionRepository gameSessionRepository;

    @Inject
    OrganizationMembershipPort organizationMembershipPort;

    @Inject
    GameSessionEventPublisher gameSessionEventPublisher;

    @Transactional
    public GameSessionResponse join(UUID organizationId, UUID sessionId, JoinSessionCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.join(command.userId(), command.nickname());
        GameSession saved = gameSessionRepository.save(session);
        gameSessionEventPublisher.publish(
                GameSessionIntegrationEvent.playerJoined(GameSessionProjectionSnapshot.from(saved)));
        return GameSessionResponse.from(saved);
    }

    @Transactional
    public GameSessionResponse leave(UUID organizationId, UUID sessionId, LeaveSessionCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.leave(command.userId());
        GameSession saved = gameSessionRepository.save(session);
        gameSessionEventPublisher.publish(
                GameSessionIntegrationEvent.playerLeft(GameSessionProjectionSnapshot.from(saved)));
        return GameSessionResponse.from(saved);
    }

    @Transactional
    public SessionPlayerResponse updateNickname(
            UUID organizationId, UUID sessionId, UpdateNicknameCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.changeNickname(command.userId(), command.nickname());
        GameSession saved = gameSessionRepository.save(session);
        gameSessionEventPublisher.publish(
                GameSessionIntegrationEvent.playerNicknameUpdated(GameSessionProjectionSnapshot.from(saved)));
        return SessionPlayerResponse.from(saved.findPlayerByUserId(command.userId()).orElseThrow());
    }
}
