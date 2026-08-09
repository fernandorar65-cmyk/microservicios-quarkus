package kahoot.clabs.application.query;

import java.util.UUID;

public record ListGameSessionsQuery(UUID organizationId, String statusCsv, UUID quizId) {
}
