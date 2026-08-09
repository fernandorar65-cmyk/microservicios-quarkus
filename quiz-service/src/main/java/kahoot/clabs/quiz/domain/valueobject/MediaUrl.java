package kahoot.clabs.quiz.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.quiz.domain.shared.DomainException;

public final class MediaUrl {

    private final String value;

    private MediaUrl(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Media URL is required");
        }
        this.value = value.trim();
    }

    public static MediaUrl of(String value) {
        return new MediaUrl(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MediaUrl that)) {
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
