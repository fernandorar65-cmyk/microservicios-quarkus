package kahoot.clabs.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.domain.shared.DomainException;

public final class FullName {

    private static final int MAX_LENGTH = 80;

    private final String firstName;
    private final String lastName;

    private FullName(String firstName, String lastName) {
        this.firstName = requireName(firstName, "First name");
        this.lastName = requireName(lastName, "Last name");
    }

    public static FullName of(String firstName, String lastName) {
        return new FullName(firstName, lastName);
    }

    private static String requireName(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainException(field + " is required");
        }
        String normalized = value.trim();
        if (normalized.length() > MAX_LENGTH) {
            throw new DomainException(field + " cannot exceed " + MAX_LENGTH + " characters");
        }
        return normalized;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FullName that)) {
            return false;
        }
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}
