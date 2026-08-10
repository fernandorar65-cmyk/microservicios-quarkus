package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.application.port.write.UserProjectionPort;
import kahoot.clabs.application.readmodel.UserImageReadModel;
import kahoot.clabs.application.readmodel.UserPermissionReadModel;
import kahoot.clabs.application.readmodel.UserReadModel;
import kahoot.clabs.application.readmodel.UserRoleReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.document.UserImageEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.UserPermissionEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.UserReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.UserRoleEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.repository.UserMongoRepository;

@ApplicationScoped
public class UserProjectionAdapter implements UserProjectionPort {

    private final UserMongoRepository userMongoRepository;

    public UserProjectionAdapter(UserMongoRepository userMongoRepository) {
        this.userMongoRepository = userMongoRepository;
    }

    @Override
    public void save(UserReadModel readModel) {
        userMongoRepository.persistOrUpdate(toDocument(readModel));
    }

    @Override
    public void deleteById(UUID id) {
        userMongoRepository.deleteById(id);
    }

    private UserReadDocument toDocument(UserReadModel readModel) {
        UserReadDocument document = new UserReadDocument();
        document.setId(readModel.getId());
        document.setEmail(readModel.getEmail());
        document.setFirstName(readModel.getFirstName());
        document.setLastName(readModel.getLastName());
        document.setFullName(readModel.getFullName());
        document.setStatus(readModel.getStatus());
        document.setPhoneNumber(readModel.getPhoneNumber());
        document.setBirthDate(readModel.getBirthDate());
        document.setBio(readModel.getBio());
        document.setLocation(readModel.getLocation());
        document.setLastLogin(readModel.getLastLogin());
        document.setRole(toRoleEmbed(readModel.getRole()));
        document.setImages(toImageEmbeds(readModel.getImages()));
        document.setCreatedAt(readModel.getCreatedAt());
        document.setUpdatedAt(readModel.getUpdatedAt());
        return document;
    }

    private UserRoleEmbed toRoleEmbed(UserRoleReadModel role) {
        if (role == null) {
            return null;
        }
        UserRoleEmbed embed = new UserRoleEmbed();
        embed.setId(role.getId());
        embed.setName(role.getName());
        embed.setType(role.getType());
        embed.setPermissions(toPermissionEmbeds(role.getPermissions()));
        return embed;
    }

    private List<UserPermissionEmbed> toPermissionEmbeds(List<UserPermissionReadModel> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }
        return permissions.stream().map(this::toPermissionEmbed).toList();
    }

    private UserPermissionEmbed toPermissionEmbed(UserPermissionReadModel permission) {
        UserPermissionEmbed embed = new UserPermissionEmbed();
        embed.setName(permission.getName());
        embed.setModule(permission.getModule());
        return embed;
    }

    private List<UserImageEmbed> toImageEmbeds(List<UserImageReadModel> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        return images.stream().map(this::toImageEmbed).toList();
    }

    private UserImageEmbed toImageEmbed(UserImageReadModel image) {
        UserImageEmbed embed = new UserImageEmbed();
        embed.setId(image.getId());
        embed.setUrl(image.getUrl());
        embed.setType(image.getType());
        embed.setAlt(image.getAlt());
        embed.setSlug(image.getSlug());
        return embed;
    }
}
