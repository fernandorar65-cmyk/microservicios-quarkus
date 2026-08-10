package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import kahoot.clabs.application.event.UserCreatedEvent;
import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.entity.Permission;
import kahoot.clabs.domain.entity.UserImages;
import kahoot.clabs.domain.valueobject.UserProfile;

public final class UserReadModels {

    private UserReadModels() {
    }

    public static UserReadModel from(UserCreatedEvent event) {
        UserReadModel model = new UserReadModel();
        model.setId(event.userId());
        model.setEmail(event.email());
        model.setFirstName(event.firstName());
        model.setLastName(event.lastName());
        model.setFullName(event.firstName() + " " + event.lastName());
        model.setStatus(event.status());
        model.setRole(null);
        model.setImages(Collections.emptyList());
        model.setCreatedAt(event.createdAt());
        model.setUpdatedAt(event.updatedAt());
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

    private static UserPermissionReadModel toPermissionReadModel(Permission permission) {
        UserPermissionReadModel model = new UserPermissionReadModel();
        model.setName(permission.getName());
        model.setModule(permission.getModule());
        return model;
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

    private static Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }

    public static List<UserImageReadModel> emptyImages() {
        return Collections.emptyList();
    }
}
