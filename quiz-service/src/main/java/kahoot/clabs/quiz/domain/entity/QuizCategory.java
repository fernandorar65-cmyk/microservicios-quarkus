package kahoot.clabs.quiz.domain.entity;

import java.util.Objects;
import java.util.UUID;

import kahoot.clabs.quiz.domain.shared.DomainException;

public final class QuizCategory {

    private final UUID quizId;
    private final UUID categoryId;

    private QuizCategory(UUID quizId, UUID categoryId) {
        if (quizId == null || categoryId == null) {
            throw new DomainException("Quiz and category ids are required");
        }
        this.quizId = quizId;
        this.categoryId = categoryId;
    }

    public static QuizCategory of(UUID quizId, UUID categoryId) {
        return new QuizCategory(quizId, categoryId);
    }

    public UUID getQuizId() {
        return quizId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof QuizCategory that)) {
            return false;
        }
        return Objects.equals(quizId, that.quizId) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId, categoryId);
    }
}
