package kahoot.clabs.application.query;

import java.util.UUID;

public record ListSessionQuestionsQuery(UUID organizationId, UUID sessionId, boolean asHost) {
}
