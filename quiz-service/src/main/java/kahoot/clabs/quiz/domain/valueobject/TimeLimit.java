package kahoot.clabs.quiz.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.quiz.domain.shared.DomainException;

public final class TimeLimit {

    private static final int MIN_SECONDS = 5;
    private static final int MAX_SECONDS = 300;
    private static final int DEFAULT_SECONDS = 30;

    private final int seconds;

    private TimeLimit(int seconds) {
        if (seconds < MIN_SECONDS || seconds > MAX_SECONDS) {
            throw new DomainException(
                    "Time limit must be between " + MIN_SECONDS + " and " + MAX_SECONDS + " seconds");
        }
        this.seconds = seconds;
    }

    public static TimeLimit ofSeconds(int seconds) {
        return new TimeLimit(seconds);
    }

    public static TimeLimit defaultValue() {
        return new TimeLimit(DEFAULT_SECONDS);
    }

    public int seconds() {
        return seconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeLimit that)) {
            return false;
        }
        return seconds == that.seconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seconds);
    }
}
