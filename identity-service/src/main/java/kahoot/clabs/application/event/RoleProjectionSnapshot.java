package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.entity.Permission;

public record RoleProjectionSnapshot(
        UUID roleId,
        String name,
        String type,
        String description,
        List<PermissionProjectionSnapshot> permissions,
        Instant createdAt,
        Instant updatedAt
) {

    public static RoleProjectionSnapshot from(Role role) {
        return new RoleProjectionSnapshot(
                role.getId(),
                role.getName(),
                role.getType().name(),
                role.getDescription(),
                role.getPermissions().stream().map(PermissionProjectionSnapshot::from).toList(),
                toInstant(role.getCreatedAt()),
                toInstant(role.getUpdatedAt()));
    }

    private static Instant toInstant(java.time.LocalDateTime value) {
        return value == null ? null : value.atZone(java.time.ZoneOffset.UTC).toInstant();
    }
}
