package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.time.ZoneOffset;

import kahoot.clabs.domain.entity.Permission;

public final class PermissionReadModels {

    private PermissionReadModels() {
    }

    public static PermissionReadModel from(Permission permission) {
        PermissionReadModel model = new PermissionReadModel();
        model.setId(permission.getId());
        model.setName(permission.getName());
        model.setDescription(permission.getDescription());
        model.setModule(permission.getModule());
        model.setCreatedAt(toInstant(permission.getCreatedAt()));
        model.setUpdatedAt(toInstant(permission.getUpdatedAt()));
        return model;
    }

    private static Instant toInstant(java.time.LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
