package kahoot.clabs.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.domain.shared.DomainException;

public final class OrganizationName {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 150;

    private final String value;

    private OrganizationName(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Organization name is required");
        }
        String normalized = value.trim();
        if (normalized.length() < MIN_LENGTH || normalized.length() > MAX_LENGTH) {
            throw new DomainException(
                    "Organization name must have between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
        }
        this.value = normalized;
    }

    public static OrganizationName of(String value) {
        return new OrganizationName(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrganizationName that)) {
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
