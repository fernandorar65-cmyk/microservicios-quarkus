package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import kahoot.clabs.application.command.HostActionCommand;
import kahoot.clabs.application.command.OpenQuestionCommand;
import kahoot.clabs.application.dto.GameSessionResponse;
import kahoot.clabs.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.repository.GameSessionRepository;

@ApplicationScoped
public class ManageSessionQuestionsUseCase {

    @Inject
    GameSessionRepository gameSessionRepository;

    @Inject
    OrganizationMembershipPort organizationMembershipPort;

    @Transactional
    public GameSessionResponse open(UUID organizationId, UUID sessionId, OpenQuestionCommand command) {
        GameSession session = loadForHost(organizationId, sessionId, command.hostUserId());
        session.openQuestion(command.questionIndex());
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public GameSessionResponse close(UUID organizationId, UUID sessionId, HostActionCommand command) {
        GameSession session = loadForHost(organizationId, sessionId, command.hostUserId());
        session.closeQuestion();
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public GameSessionResponse next(UUID organizationId, UUID sessionId, HostActionCommand command) {
        GameSession session = loadForHost(organizationId, sessionId, command.hostUserId());
        session.nextQuestion();
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    private GameSession loadForHost(UUID organizationId, UUID sessionId, UUID hostUserId) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, hostUserId);
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.ensureHost(hostUserId);
        return session;
    }
}
