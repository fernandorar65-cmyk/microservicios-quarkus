package kahoot.clabs.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.domain.shared.DomainException;

/**
 * URL-friendly identifier of a tenant: lowercase words separated by single hyphens.
 */
public final class OrganizationSlug {

    private static final String SLUG_PATTERN = "^[a-z0-9]+(-[a-z0-9]+)*$";
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    private final String value;

    private OrganizationSlug(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Organization slug is required");
        }
        String normalized = value.trim().toLowerCase();
        if (normalized.length() < MIN_LENGTH || normalized.length() > MAX_LENGTH) {
            throw new DomainException(
                    "Organization slug must have between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
        }
        if (!normalized.matches(SLUG_PATTERN)) {
            throw new DomainException("Organization slug only accepts lowercase letters, numbers and hyphens");
        }
        this.value = normalized;
    }

    public static OrganizationSlug of(String value) {
        return new OrganizationSlug(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrganizationSlug that)) {
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
