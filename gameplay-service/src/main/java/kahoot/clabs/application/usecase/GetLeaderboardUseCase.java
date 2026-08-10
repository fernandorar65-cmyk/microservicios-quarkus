package kahoot.clabs.application.usecase;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import kahoot.clabs.application.dto.LeaderboardEntryResponse;
import kahoot.clabs.application.port.out.read.LeaderboardReadPort;
import kahoot.clabs.application.query.GetLeaderboardQuery;
import kahoot.clabs.application.readmodel.LeaderboardReadModel;
import kahoot.clabs.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class GetLeaderboardUseCase {

    @Inject
    LeaderboardReadPort leaderboardReadPort;

    public List<LeaderboardEntryResponse> execute(GetLeaderboardQuery query) {
        LeaderboardReadModel leaderboard = leaderboardReadPort.findBySessionId(query.sessionId())
                .orElseThrow(() -> new GameSessionNotFoundException(query.sessionId()));
        if (!leaderboard.getOrganizationId().equals(query.organizationId())) {
            throw new DomainException(
                    "Game session does not belong to organization: " + query.organizationId());
        }
        return LeaderboardEntryResponse.from(leaderboard);
    }
}
