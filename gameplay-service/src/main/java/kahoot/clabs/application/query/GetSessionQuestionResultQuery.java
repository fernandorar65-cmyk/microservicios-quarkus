package kahoot.clabs.application.query;

import java.util.UUID;

public record GetSessionQuestionResultQuery(UUID organizationId, UUID sessionId, UUID sessionQuestionId) {
}
