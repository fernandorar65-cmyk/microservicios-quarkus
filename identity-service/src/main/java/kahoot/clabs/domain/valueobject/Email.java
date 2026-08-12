package kahoot.clabs.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.domain.shared.DomainException;

public final class Email {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private final String value;

    private Email(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Email is required");
        }
        String normalized = value.trim().toLowerCase();
        if (!normalized.matches(EMAIL_PATTERN)) {
            throw new DomainException("Invalid email");
        }
        this.value = normalized;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Email email)) {
            return false;
        }
        return Objects.equals(value, email.value);
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
