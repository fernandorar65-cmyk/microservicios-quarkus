package kahoot.clabs.quiz.application.query;

import java.util.UUID;

public record GetQuizQuery(UUID organizationId, UUID quizId) {
}
