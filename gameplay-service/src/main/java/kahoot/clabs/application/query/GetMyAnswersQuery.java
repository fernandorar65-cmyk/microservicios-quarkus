package kahoot.clabs.application.query;

import java.util.UUID;

public record GetMyAnswersQuery(UUID organizationId, UUID sessionId, UUID userId) {
}
