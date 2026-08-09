package kahoot.clabs.domain.entity;

import java.util.UUID;

import kahoot.clabs.domain.shared.DomainException;

public final class RolePermission {

    private final UUID roleId;
    private final UUID permissionId;

    private RolePermission(UUID roleId, UUID permissionId) {
        if (roleId == null || permissionId == null) {
            throw new DomainException("Role id and permission id are required");
        }
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public static RolePermission create(UUID roleId, UUID permissionId) {
        return new RolePermission(roleId, permissionId);
    }

    public UUID getRoleId() {
        return roleId;
    }

    public UUID getPermissionId() {
        return permissionId;
    }
}
