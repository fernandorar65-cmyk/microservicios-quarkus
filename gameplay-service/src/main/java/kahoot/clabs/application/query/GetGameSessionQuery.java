package kahoot.clabs.application.query;

import java.util.UUID;

public record GetGameSessionQuery(UUID organizationId, UUID sessionId) {
}
