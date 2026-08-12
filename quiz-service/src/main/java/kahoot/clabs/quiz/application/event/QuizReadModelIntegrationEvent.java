package kahoot.clabs.quiz.application.event;

import java.util.UUID;

import kahoot.clabs.quiz.application.readmodel.CategoryReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizReadModel;

public record QuizReadModelIntegrationEvent(
        UUID eventId,
        String eventType,
        int version,
        java.time.Instant occurredAt,
        UUID aggregateId,
        QuizReadModel quiz,
        CategoryReadModel category
) {

    public static final int VERSION = 1;
    public static final String QUIZ_UPSERTED = "QuizUpserted";
    public static final String QUIZ_DELETED = "QuizDeleted";
    public static final String CATEGORY_UPSERTED = "CategoryUpserted";
    public static final String CATEGORY_DELETED = "CategoryDeleted";

    public static QuizReadModelIntegrationEvent quizUpserted(QuizReadModel quiz) {
        return new QuizReadModelIntegrationEvent(
                UUID.randomUUID(),
                QUIZ_UPSERTED,
                VERSION,
                java.time.Instant.now(),
                quiz.getId(),
                quiz,
                null);
    }

    public static QuizReadModelIntegrationEvent quizDeleted(UUID quizId) {
        return new QuizReadModelIntegrationEvent(
                UUID.randomUUID(),
                QUIZ_DELETED,
                VERSION,
                java.time.Instant.now(),
                quizId,
                null,
                null);
    }

    public static QuizReadModelIntegrationEvent categoryUpserted(CategoryReadModel category) {
        return new QuizReadModelIntegrationEvent(
                UUID.randomUUID(),
                CATEGORY_UPSERTED,
                VERSION,
                java.time.Instant.now(),
                category.getId(),
                null,
                category);
    }

    public static QuizReadModelIntegrationEvent categoryDeleted(UUID categoryId) {
        return new QuizReadModelIntegrationEvent(
                UUID.randomUUID(),
                CATEGORY_DELETED,
                VERSION,
                java.time.Instant.now(),
                categoryId,
                null,
                null);
    }
}
