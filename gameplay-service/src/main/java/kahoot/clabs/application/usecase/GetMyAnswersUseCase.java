package kahoot.clabs.application.usecase;

import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import kahoot.clabs.application.dto.PlayerAnswerResponse;
import kahoot.clabs.application.port.read.GameSessionReadPort;
import kahoot.clabs.application.query.GetMyAnswersQuery;
import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class GetMyAnswersUseCase {

    @Inject
    GameSessionReadPort gameSessionReadPort;

    public List<PlayerAnswerResponse> execute(GetMyAnswersQuery query) {
        GameSessionReadModel session = requireSession(query.organizationId(), query.sessionId());
        GameSessionReadModel.SessionPlayer player = session.getPlayers().stream()
                .filter(candidate -> candidate.getUserId().equals(query.userId()))
                .findFirst()
                .orElseThrow(() -> new DomainException("Player not found in session: " + query.userId()));
        return session.getPlayerAnswers().stream()
                .filter(answer -> player.getId().equals(answer.getSessionPlayerId()))
                .map(PlayerAnswerResponse::from)
                .toList();
    }

    private GameSessionReadModel requireSession(UUID organizationId, UUID sessionId) {
        return gameSessionReadPort.findById(sessionId)
                .map(session -> {
                    if (!session.getOrganizationId().equals(organizationId)) {
                        throw new DomainException(
                                "Game session does not belong to organization: " + organizationId);
                    }
                    return session;
                })
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
    }
}
