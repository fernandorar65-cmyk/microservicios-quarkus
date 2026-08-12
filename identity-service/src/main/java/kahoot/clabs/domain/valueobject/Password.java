package kahoot.clabs.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.domain.shared.DomainException;

public final class Password {

    private static final int MIN_LENGTH = 8;

    private final String hashedValue;

    private Password(String hashedValue) {
        if (hashedValue == null || hashedValue.isBlank()) {
            throw new DomainException("Password hash is required");
        }
        this.hashedValue = hashedValue;
    }

    public static void assertValidRaw(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_LENGTH) {
            throw new DomainException("Password must have at least " + MIN_LENGTH + " characters");
        }
    }

    public static Password fromRawTemporarily(String rawPassword) {
        assertValidRaw(rawPassword);
        return new Password(rawPassword);
    }

    public static Password fromHashed(String hashedValue) {
        return new Password(hashedValue);
    }

    public String hashedValue() {
        return hashedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Password password)) {
            return false;
        }
        return Objects.equals(hashedValue, password.hashedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashedValue);
    }
}
