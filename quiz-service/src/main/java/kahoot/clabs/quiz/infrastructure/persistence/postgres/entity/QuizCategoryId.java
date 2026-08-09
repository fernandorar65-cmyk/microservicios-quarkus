package kahoot.clabs.quiz.infrastructure.persistence.postgres.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Composite primary key for quiz_categories join table.
 * Property names must match the {@code @Id} attributes on {@link QuizCategoryJpaEntity}.
 */
public class QuizCategoryId implements Serializable {

    private UUID quiz;
    private UUID category;

    public QuizCategoryId() {
    }

    public QuizCategoryId(UUID quiz, UUID category) {
        this.quiz = quiz;
        this.category = category;
    }

    public UUID getQuiz() {
        return quiz;
    }

    public void setQuiz(UUID quiz) {
        this.quiz = quiz;
    }

    public UUID getCategory() {
        return category;
    }

    public void setCategory(UUID category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuizCategoryId that)) {
            return false;
        }
        return Objects.equals(quiz, that.quiz)
                && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quiz, category);
    }
}
