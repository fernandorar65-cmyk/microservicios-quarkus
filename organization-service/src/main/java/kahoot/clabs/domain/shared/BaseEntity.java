package kahoot.clabs.domain.shared;

import java.util.Objects;
import java.util.UUID;

public abstract class BaseEntity {

    private final UUID id;

    protected BaseEntity(UUID id) {
        this.id = id != null ? id : UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        return id.equals(((BaseEntity) other).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
