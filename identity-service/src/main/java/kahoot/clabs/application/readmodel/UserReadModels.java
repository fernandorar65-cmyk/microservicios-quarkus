package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import kahoot.clabs.application.event.UserImagePayload;
import kahoot.clabs.application.event.UserPermissionPayload;
import kahoot.clabs.application.event.UserProjectionSnapshot;
import kahoot.clabs.application.event.UserRolePayload;
import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.entity.Permission;
import kahoot.clabs.domain.entity.UserImages;
import kahoot.clabs.domain.valueobject.UserProfile;

public final class UserReadModels {

    private UserReadModels() {
    }

    public static UserReadModel from(UserProjectionSnapshot snapshot) {
        UserReadModel model = new UserReadModel();
        model.setId(snapshot.userId());
        model.setEmail(snapshot.email());
        model.setFirstName(snapshot.firstName());
        model.setLastName(snapshot.lastName());
        model.setFullName(snapshot.firstName() + " " + snapshot.lastName());
        model.setStatus(snapshot.status());
        model.setPhoneNumber(snapshot.phoneNumber());
        model.setBirthDate(snapshot.birthDate());
        model.setBio(snapshot.bio());
        model.setLocation(snapshot.location());
        model.setLastLogin(snapshot.lastLogin());
        model.setRole(toRoleReadModel(snapshot.role()));
        model.setImages(toImageReadModels(snapshot.images()));
        model.setCreatedAt(snapshot.createdAt());
        model.setUpdatedAt(snapshot.updatedAt());
        return model;
    }

    public static UserReadModel from(User user) {
        return from(user, null);
    }

    public static UserReadModel from(User user, Role role) {
        UserProfile profile = user.getProfile();
        UserReadModel model = new UserReadModel();
        model.setId(user.getId());
        model.setEmail(user.getEmail().value());
        model.setFirstName(user.getFullName().firstName());
        model.setLastName(user.getFullName().lastName());
        model.setFullName(user.getFullName().fullName());
        model.setStatus(user.getStatus().name());
        model.setPhoneNumber(profile.phoneNumber());
        model.setBirthDate(profile.birthDate());
        model.setBio(profile.bio());
        model.setLocation(profile.location());
        model.setLastLogin(toInstant(user.getLastLogin()));
        model.setRole(toRoleReadModel(role));
        model.setImages(user.getImages().stream().map(UserReadModels::toImageReadModel).toList());
        model.setCreatedAt(toInstant(user.getCreatedAt()));
        model.setUpdatedAt(toInstant(user.getUpdatedAt()));
        return model;
    }

    private static UserRoleReadModel toRoleReadModel(Role role) {
        if (role == null) {
            return null;
        }
        UserRoleReadModel model = new UserRoleReadModel();
        model.setId(role.getId());
        model.setName(role.getName());
        model.setType(role.getType().name());
        model.setPermissions(role.getPermissions().stream().map(UserReadModels::toPermissionReadModel).toList());
        return model;
    }

    private static UserRoleReadModel toRoleReadModel(UserRolePayload role) {
        if (role == null) {
            return null;
        }
        UserRoleReadModel model = new UserRoleReadModel();
        model.setId(role.id());
        model.setName(role.name());
        model.setType(role.type());
        model.setPermissions(toPermissionReadModels(role.permissions()));
        return model;
    }

    private static List<UserPermissionReadModel> toPermissionReadModels(List<UserPermissionPayload> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }
        return permissions.stream().map(UserReadModels::toPermissionReadModel).toList();
    }

    private static UserPermissionReadModel toPermissionReadModel(Permission permission) {
        UserPermissionReadModel model = new UserPermissionReadModel();
        model.setName(permission.getName());
        model.setModule(permission.getModule());
        return model;
    }

    private static UserPermissionReadModel toPermissionReadModel(UserPermissionPayload permission) {
        UserPermissionReadModel model = new UserPermissionReadModel();
        model.setName(permission.name());
        model.setModule(permission.module());
        return model;
    }

    private static List<UserImageReadModel> toImageReadModels(List<UserImagePayload> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        return images.stream().map(UserReadModels::toImageReadModel).toList();
    }

    private static UserImageReadModel toImageReadModel(UserImages image) {
        UserImageReadModel model = new UserImageReadModel();
        model.setId(image.getId());
        model.setUrl(image.getUrl());
        model.setType(image.getType());
        model.setAlt(image.getAlt());
        model.setSlug(image.getSlug());
        return model;
    }

    private static UserImageReadModel toImageReadModel(UserImagePayload image) {
        UserImageReadModel model = new UserImageReadModel();
        model.setId(image.id());
        model.setUrl(image.url());
        model.setType(image.type());
        model.setAlt(image.alt());
        model.setSlug(image.slug());
        return model;
    }

    private static Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }

    public static List<UserImageReadModel> emptyImages() {
        return Collections.emptyList();
    }
}
