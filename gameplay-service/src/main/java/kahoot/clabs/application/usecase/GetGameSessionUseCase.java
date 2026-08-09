package kahoot.clabs.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import kahoot.clabs.application.dto.GameSessionResponse;
import kahoot.clabs.application.port.out.read.GameSessionReadPort;
import kahoot.clabs.application.query.GetGameSessionQuery;
import kahoot.clabs.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class GetGameSessionUseCase {

    @Inject
    GameSessionReadPort gameSessionReadPort;

    public GameSessionResponse execute(GetGameSessionQuery query) {
        return gameSessionReadPort.findById(query.sessionId())
                .map(session -> {
                    if (!session.getOrganizationId().equals(query.organizationId())) {
                        throw new DomainException(
                                "Game session does not belong to organization: " + query.organizationId());
                    }
                    return GameSessionResponse.from(session);
                })
                .orElseThrow(() -> new GameSessionNotFoundException(query.sessionId()));
    }
}
