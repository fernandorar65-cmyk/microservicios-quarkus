package kahoot.clabs.application.query;

import java.util.UUID;

public record ListSessionPlayersQuery(UUID organizationId, UUID sessionId) {
}
