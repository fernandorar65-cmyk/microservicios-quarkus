package kahoot.clabs.quiz.domain.valueobject;

import java.time.Duration;
import java.util.Objects;

import kahoot.clabs.quiz.domain.shared.DomainException;

public final class EstimatedTime {

    private static final long MAX_MINUTES = 180;

    private final Duration value;

    private EstimatedTime(Duration value) {
        if (value == null || value.isNegative() || value.isZero()) {
            throw new DomainException("Estimated time must be greater than zero");
        }
        if (value.toMinutes() > MAX_MINUTES) {
            throw new DomainException("Maximum estimated time is " + MAX_MINUTES + " minutes");
        }
        this.value = value;
    }

    public static EstimatedTime ofMinutes(long minutes) {
        return new EstimatedTime(Duration.ofMinutes(minutes));
    }

    public Duration value() {
        return value;
    }

    public long toMinutes() {
        return value.toMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EstimatedTime that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
