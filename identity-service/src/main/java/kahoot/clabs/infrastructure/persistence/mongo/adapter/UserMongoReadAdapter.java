package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.application.port.read.UserReadPort;
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
public class UserMongoReadAdapter implements UserReadPort {

    private final UserMongoRepository userMongoRepository;

    public UserMongoReadAdapter(UserMongoRepository userMongoRepository) {
        this.userMongoRepository = userMongoRepository;
    }

    @Override
    public Optional<UserReadModel> findById(UUID id) {
        return userMongoRepository.findByIdOptional(id).map(this::toReadModel);
    }

    @Override
    public Optional<UserReadModel> findByEmail(String email) {
        return userMongoRepository.find("email", email).firstResultOptional().map(this::toReadModel);
    }

    private UserReadModel toReadModel(UserReadDocument document) {
        UserReadModel readModel = new UserReadModel();
        readModel.setId(document.getId());
        readModel.setEmail(document.getEmail());
        readModel.setFirstName(document.getFirstName());
        readModel.setLastName(document.getLastName());
        readModel.setFullName(document.getFullName());
        readModel.setStatus(document.getStatus());
        readModel.setPhoneNumber(document.getPhoneNumber());
        readModel.setBirthDate(document.getBirthDate());
        readModel.setBio(document.getBio());
        readModel.setLocation(document.getLocation());
        readModel.setLastLogin(document.getLastLogin());
        readModel.setRole(toRoleReadModel(document.getRole()));
        readModel.setImages(toImageReadModels(document.getImages()));
        readModel.setCreatedAt(document.getCreatedAt());
        readModel.setUpdatedAt(document.getUpdatedAt());
        return readModel;
    }

    private UserRoleReadModel toRoleReadModel(UserRoleEmbed embed) {
        if (embed == null) {
            return null;
        }

        UserRoleReadModel readModel = new UserRoleReadModel();
        readModel.setId(embed.getId());
        readModel.setName(embed.getName());
        readModel.setType(embed.getType());
        readModel.setPermissions(toPermissionReadModels(embed.getPermissions()));
        return readModel;
    }

    private List<UserPermissionReadModel> toPermissionReadModels(List<UserPermissionEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toPermissionReadModel).toList();
    }

    private UserPermissionReadModel toPermissionReadModel(UserPermissionEmbed embed) {
        UserPermissionReadModel readModel = new UserPermissionReadModel();
        readModel.setName(embed.getName());
        readModel.setModule(embed.getModule());
        return readModel;
    }

    private List<UserImageReadModel> toImageReadModels(List<UserImageEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toImageReadModel).toList();
    }

    private UserImageReadModel toImageReadModel(UserImageEmbed embed) {
        UserImageReadModel readModel = new UserImageReadModel();
        readModel.setId(embed.getId());
        readModel.setUrl(embed.getUrl());
        readModel.setType(embed.getType());
        readModel.setAlt(embed.getAlt());
        readModel.setSlug(embed.getSlug());
        return readModel;
    }
}
