package kahoot.clabs.application.query;

import java.util.UUID;

public record GetLeaderboardQuery(UUID organizationId, UUID sessionId) {
}
