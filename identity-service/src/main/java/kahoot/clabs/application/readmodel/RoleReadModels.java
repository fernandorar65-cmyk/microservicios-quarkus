package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.time.ZoneOffset;

import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.entity.Permission;

public final class RoleReadModels {

    private RoleReadModels() {
    }

    public static RoleReadModel from(Role role) {
        RoleReadModel model = new RoleReadModel();
        model.setId(role.getId());
        model.setName(role.getName());
        model.setType(role.getType().name());
        model.setDescription(role.getDescription());
        model.setPermissions(role.getPermissions().stream().map(RoleReadModels::toPermission).toList());
        model.setCreatedAt(toInstant(role.getCreatedAt()));
        model.setUpdatedAt(toInstant(role.getUpdatedAt()));
        return model;
    }

    private static RolePermissionReadModel toPermission(Permission permission) {
        RolePermissionReadModel model = new RolePermissionReadModel();
        model.setId(permission.getId());
        model.setName(permission.getName());
        model.setDescription(permission.getDescription());
        model.setModule(permission.getModule());
        return model;
    }

    private static Instant toInstant(java.time.LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
