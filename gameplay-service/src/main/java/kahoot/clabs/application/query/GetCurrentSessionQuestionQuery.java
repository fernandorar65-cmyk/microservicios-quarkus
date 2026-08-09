package kahoot.clabs.application.query;

import java.util.UUID;

public record GetCurrentSessionQuestionQuery(UUID organizationId, UUID sessionId) {
}
