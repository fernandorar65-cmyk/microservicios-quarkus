package kahoot.clabs.quiz.application.event;

import java.util.UUID;

public record QuizReadModelDeletedEvent(UUID quizId) {
}
