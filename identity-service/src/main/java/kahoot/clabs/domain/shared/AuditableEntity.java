package kahoot.clabs.domain.shared;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class AuditableEntity extends BaseEntity {

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected AuditableEntity(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id);
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    protected void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
