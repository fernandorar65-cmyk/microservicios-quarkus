package kahoot.clabs.domain.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.domain.entity.Permission;
import kahoot.clabs.domain.valueobject.RoleType;
import kahoot.clabs.domain.shared.AggregateRoot;
import kahoot.clabs.domain.shared.DomainException;

public class Role extends AggregateRoot {

    private String name;
    private final RoleType type;
    private String description;
    private final List<Permission> permissions = new ArrayList<>();

    private Role(UUID id, String name, RoleType type, String description) {
        this(id, name, type, description, null, null);
    }

    private Role(
            UUID id,
            String name,
            RoleType type,
            String description,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        if (name == null || name.isBlank()) {
            throw new DomainException("Role name is required");
        }
        if (type == null) {
            throw new DomainException("Role type is required");
        }
        this.name = name.trim();
        this.type = type;
        this.description = description;
    }

    public static Role create(String name, RoleType type, String description) {
        return new Role(null, name, type, description);
    }

    public static Role rehydrate(UUID id, String name, RoleType type, String description, List<Permission> permissions) {
        Role role = new Role(id, name, type, description);
        if (permissions != null) {
            role.permissions.addAll(permissions);
        }
        return role;
    }

    public static Role rehydrate(
            UUID id,
            String name,
            RoleType type,
            String description,
            List<Permission> permissions,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        Role role = new Role(id, name, type, description, createdAt, updatedAt);
        if (permissions != null) {
            role.permissions.addAll(permissions);
        }
        return role;
    }

    public void rename(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Role name is required");
        }
        this.name = name.trim();
        touch();
    }

    public void changeDescription(String description) {
        this.description = description;
        touch();
    }

    public void addPermission(Permission permission) {
        if (permission == null) {
            throw new DomainException("Permission is required");
        }
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            touch();
        }
    }

    public void removePermission(Permission permission) {
        if (permissions.remove(permission)) {
            touch();
        }
    }

    public boolean hasPermission(String permissionName) {
        return permissions.stream().anyMatch(permission -> permission.getName().equalsIgnoreCase(permissionName));
    }

    public String getName() {
        return name;
    }

    public RoleType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<Permission> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }
}
