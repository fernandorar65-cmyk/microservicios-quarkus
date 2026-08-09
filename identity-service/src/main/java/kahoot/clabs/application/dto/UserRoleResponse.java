package kahoot.clabs.application.dto;

import kahoot.clabs.domain.entity.Permission;

public record UserRoleResponse(
        String name,
        String description
) {

    public static UserRoleResponse from(Permission permission) {
        return new UserRoleResponse(
                permission.getName(),
                permission.getDescription());
    }
}
