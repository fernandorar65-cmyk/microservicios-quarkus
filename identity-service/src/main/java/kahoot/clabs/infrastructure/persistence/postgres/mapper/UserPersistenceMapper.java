package kahoot.clabs.infrastructure.persistence.postgres.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.entity.UserImages;
import kahoot.clabs.domain.valueobject.Email;
import kahoot.clabs.domain.valueobject.FullName;
import kahoot.clabs.domain.valueobject.Password;
import kahoot.clabs.domain.valueobject.UserProfile;
import kahoot.clabs.domain.valueobject.UserStatus;
import kahoot.clabs.infrastructure.persistence.postgres.entity.UserImageJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.UserJpaEntity;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    public static UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail().value());
        entity.setPasswordHash(user.getPassword().hashedValue());
        entity.setFirstName(user.getFullName().firstName());
        entity.setLastName(user.getFullName().lastName());
        entity.setStatus(user.getStatus().name());

        UserProfile profile = user.getProfile();
        if (profile != null) {
            entity.setPhoneNumber(profile.phoneNumber());
            entity.setBirthDate(profile.birthDate());
            entity.setBio(profile.bio());
            entity.setLocation(profile.location());
        }

        entity.setLastLogin(user.getLastLogin());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        List<UserImageJpaEntity> imageEntities = new ArrayList<>();
        for (UserImages image : user.getImages()) {
            imageEntities.add(toImageEntity(image, entity));
        }
        entity.setImages(imageEntities);
        return entity;
    }

    public static User toDomain(UserJpaEntity entity, UUID roleId) {
        UserProfile profile = UserProfile.builder()
                .phoneNumber(entity.getPhoneNumber())
                .birthDate(entity.getBirthDate())
                .bio(entity.getBio())
                .location(entity.getLocation())
                .build();

        List<UserImages> images = entity.getImages() == null
                ? List.of()
                : entity.getImages().stream().map(UserPersistenceMapper::toImageDomain).toList();

        return User.rehydrate(
                entity.getId(),
                roleId,
                Email.of(entity.getEmail()),
                FullName.of(entity.getFirstName(), entity.getLastName()),
                Password.fromHashed(entity.getPasswordHash()),
                profile,
                UserStatus.valueOf(entity.getStatus()),
                images,
                entity.getLastLogin(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private static UserImageJpaEntity toImageEntity(UserImages image, UserJpaEntity user) {
        UserImageJpaEntity entity = new UserImageJpaEntity();
        entity.setId(image.getId());
        entity.setUser(user);
        entity.setUrl(image.getUrl());
        entity.setType(image.getType());
        entity.setAlt(image.getAlt());
        entity.setSlug(image.getSlug());
        entity.setCreatedAt(image.getCreatedAt());
        entity.setUpdatedAt(image.getUpdatedAt());
        return entity;
    }

    private static UserImages toImageDomain(UserImageJpaEntity entity) {
        return UserImages.rehydrate(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUrl(),
                entity.getType(),
                entity.getAlt(),
                entity.getSlug(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
