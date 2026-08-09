package kahoot.clabs.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import kahoot.clabs.domain.shared.AuditableEntity;
import kahoot.clabs.domain.shared.DomainException;

public class Permission extends AuditableEntity {

    private final String name;
    private final String description;
    private final String module;

    private Permission(UUID id, String name, String description, String module) {
        this(id, name, description, module, null, null);
    }

    private Permission(
            UUID id,
            String name,
            String description,
            String module,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        if (name == null || name.isBlank()) {
            throw new DomainException("Permission name is required");
        }
        if (module == null || module.isBlank()) {
            throw new DomainException("Permission module is required");
        }
        this.name = name.trim().toUpperCase();
        this.description = description;
        this.module = module.trim().toLowerCase();
    }

    public static Permission create(String name, String description, String module) {
        return new Permission(null, name, description, module);
    }

    public static Permission reconstitute(UUID id, String name, String description, String module) {
        return new Permission(id, name, description, module);
    }

    public static Permission rehydrate(
            UUID id,
            String name,
            String description,
            String module,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new Permission(id, name, description, module, createdAt, updatedAt);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getModule() {
        return module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission that)) {
            return false;
        }
        return Objects.equals(name, that.name) && Objects.equals(module, that.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, module);
    }
}
