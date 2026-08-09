package kahoot.clabs.quiz.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.quiz.domain.shared.DomainException;

public final class Points {

    private static final int MIN = 0;
    private static final int MAX = 10000;
    private static final int DEFAULT = 1000;

    private final int value;

    private Points(int value) {
        if (value < MIN || value > MAX) {
            throw new DomainException("Points must be between " + MIN + " and " + MAX);
        }
        this.value = value;
    }

    public static Points of(int value) {
        return new Points(value);
    }

    public static Points defaultValue() {
        return new Points(DEFAULT);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Points that)) {
            return false;
        }
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
