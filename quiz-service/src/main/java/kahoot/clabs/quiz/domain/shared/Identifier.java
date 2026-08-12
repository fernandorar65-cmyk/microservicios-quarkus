package kahoot.clabs.quiz.domain.shared;

import java.util.Objects;
import java.util.UUID;

public abstract class Identifier {

    private final UUID value;

    protected Identifier(UUID value) {
        if (value == null) {
            throw new DomainException("Identifier value is required");
        }
        this.value = value;
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        return value.equals(((Identifier) other).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
