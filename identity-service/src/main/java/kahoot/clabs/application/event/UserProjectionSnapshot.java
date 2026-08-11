package kahoot.clabs.application.event;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.entity.Permission;
import kahoot.clabs.domain.entity.UserImages;
import kahoot.clabs.domain.valueobject.UserProfile;

/**
 * Snapshot of user data needed to upsert the Mongo read model (no secrets).
 */
public record UserProjectionSnapshot(
        java.util.UUID userId,
        String email,
        String firstName,
        String lastName,
        String status,
        String phoneNumber,
        LocalDate birthDate,
        String bio,
        String location,
        Instant lastLogin,
        UserRolePayload role,
        List<UserImagePayload> images,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserProjectionSnapshot from(User user, Role role) {
        UserProfile profile = user.getProfile();
        return new UserProjectionSnapshot(
                user.getId(),
                user.getEmail().value(),
                user.getFullName().firstName(),
                user.getFullName().lastName(),
                user.getStatus().name(),
                profile.phoneNumber(),
                profile.birthDate(),
                profile.bio(),
                profile.location(),
                toInstant(user.getLastLogin()),
                toRolePayload(role),
                user.getImages().stream().map(UserProjectionSnapshot::toImagePayload).toList(),
                toInstant(user.getCreatedAt()),
                toInstant(user.getUpdatedAt()));
    }

    private static UserRolePayload toRolePayload(Role role) {
        if (role == null) {
            return null;
        }
        List<UserPermissionPayload> permissions = role.getPermissions() == null
                ? Collections.emptyList()
                : role.getPermissions().stream().map(UserProjectionSnapshot::toPermissionPayload).toList();
        return new UserRolePayload(role.getId(), role.getName(), role.getType().name(), permissions);
    }

    private static UserPermissionPayload toPermissionPayload(Permission permission) {
        return new UserPermissionPayload(permission.getName(), permission.getModule());
    }

    private static UserImagePayload toImagePayload(UserImages image) {
        return new UserImagePayload(
                image.getId(),
                image.getUrl(),
                image.getType(),
                image.getAlt(),
                image.getSlug());
    }

    private static Instant toInstant(java.time.LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
