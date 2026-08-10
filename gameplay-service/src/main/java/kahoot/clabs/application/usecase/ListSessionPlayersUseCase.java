package kahoot.clabs.application.usecase;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import kahoot.clabs.application.dto.SessionPlayerResponse;
import kahoot.clabs.application.port.read.GameSessionReadPort;
import kahoot.clabs.application.query.ListSessionPlayersQuery;
import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class ListSessionPlayersUseCase {

    @Inject
    GameSessionReadPort gameSessionReadPort;

    public List<SessionPlayerResponse> execute(ListSessionPlayersQuery query) {
        GameSessionReadModel session = gameSessionReadPort.findById(query.sessionId())
                .orElseThrow(() -> new DomainException("Game session not found: " + query.sessionId()));
        if (!session.getOrganizationId().equals(query.organizationId())) {
            throw new DomainException("Game session does not belong to organization: " + query.organizationId());
        }
        return session.getPlayers().stream().map(SessionPlayerResponse::from).toList();
    }
}
