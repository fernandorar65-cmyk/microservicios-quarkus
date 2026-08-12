package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.UUID;

import kahoot.clabs.domain.entity.Permission;

public record PermissionProjectionSnapshot(
        UUID permissionId,
        String name,
        String description,
        String module,
        Instant createdAt,
        Instant updatedAt
) {

    public static PermissionProjectionSnapshot from(Permission permission) {
        return new PermissionProjectionSnapshot(
                permission.getId(),
                permission.getName(),
                permission.getDescription(),
                permission.getModule(),
                toInstant(permission.getCreatedAt()),
                toInstant(permission.getUpdatedAt()));
    }

    private static Instant toInstant(java.time.LocalDateTime value) {
        return value == null ? null : value.atZone(java.time.ZoneOffset.UTC).toInstant();
    }
}
