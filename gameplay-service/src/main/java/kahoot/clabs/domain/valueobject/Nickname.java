package kahoot.clabs.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.domain.shared.DomainException;

public final class Nickname {

    private static final int MAX_LENGTH = 30;

    private final String value;

    private Nickname(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Nickname is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_LENGTH) {
            throw new DomainException("Nickname cannot exceed " + MAX_LENGTH + " characters");
        }
        this.value = trimmed;
    }

    public static Nickname of(String value) {
        return new Nickname(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Nickname that)) {
            return false;
        }
        return value.equalsIgnoreCase(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
