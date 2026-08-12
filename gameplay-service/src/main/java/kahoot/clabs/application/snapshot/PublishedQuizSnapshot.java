package kahoot.clabs.application.snapshot;

import java.util.List;
import java.util.UUID;

public record PublishedQuizSnapshot(
        UUID quizId,
        UUID organizationId,
        List<QuestionSnapshot> questions) {

    public record QuestionSnapshot(
            UUID id,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            String title,
            String description,
            String type,
            List<AnswerOptionSnapshot> options) {
    }

    public record AnswerOptionSnapshot(
            UUID id,
            String text,
            boolean correct,
            int orderIndex) {
    }
}
