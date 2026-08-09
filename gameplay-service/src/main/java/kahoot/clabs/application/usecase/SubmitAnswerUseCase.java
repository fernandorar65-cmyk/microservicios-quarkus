package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import kahoot.clabs.application.command.SubmitAnswerCommand;
import kahoot.clabs.application.dto.PlayerAnswerResponse;
import kahoot.clabs.application.port.out.integration.OrganizationMembershipPort;
import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.entity.PlayerAnswer;
import kahoot.clabs.domain.repository.GameSessionRepository;

@ApplicationScoped
public class SubmitAnswerUseCase {

    @Inject
    GameSessionRepository gameSessionRepository;

    @Inject
    OrganizationMembershipPort organizationMembershipPort;

    @Transactional
    public PlayerAnswerResponse execute(UUID organizationId, UUID sessionId, SubmitAnswerCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        PlayerAnswer answer = session.submitAnswer(command.userId(), command.sessionAnswerOptionId());
        gameSessionRepository.save(session);
        return PlayerAnswerResponse.from(answer);
    }
}
