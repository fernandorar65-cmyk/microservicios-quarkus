package kahoot.clabs.quiz.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.quiz.domain.shared.DomainException;

public final class QuizTitle {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 200;

    private final String value;

    private QuizTitle(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Quiz title is required");
        }
        String normalized = value.trim();
        if (normalized.length() < MIN_LENGTH) {
            throw new DomainException("Quiz title must have at least " + MIN_LENGTH + " characters");
        }
        if (normalized.length() > MAX_LENGTH) {
            throw new DomainException("Quiz title cannot exceed " + MAX_LENGTH + " characters");
        }
        this.value = normalized;
    }

    public static QuizTitle of(String value) {
        return new QuizTitle(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuizTitle that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
