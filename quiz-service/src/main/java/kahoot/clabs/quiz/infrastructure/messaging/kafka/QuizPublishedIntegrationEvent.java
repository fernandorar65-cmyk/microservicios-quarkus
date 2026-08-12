package kahoot.clabs.quiz.infrastructure.messaging.kafka;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record QuizPublishedIntegrationEvent(
        UUID eventId,
        String eventType,
        int version,
        Instant occurredAt,
        UUID aggregateId,
        Payload payload) {

    public static final String EVENT_TYPE = "QuizPublished";
    public static final int VERSION = 1;

    public record Payload(
            UUID quizId,
            UUID organizationId,
            UUID publishedById,
            String title,
            List<QuestionPayload> questions) {
    }

    public record QuestionPayload(
            UUID id,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            String title,
            String description,
            String type,
            List<AnswerOptionPayload> options) {
    }

    public record AnswerOptionPayload(
            UUID id,
            String text,
            boolean correct,
            int orderIndex) {
    }
}
