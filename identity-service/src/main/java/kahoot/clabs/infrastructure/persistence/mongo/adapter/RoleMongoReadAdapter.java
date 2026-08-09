package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.application.port.out.read.RoleReadPort;
import kahoot.clabs.application.readmodel.RolePermissionReadModel;
import kahoot.clabs.application.readmodel.RoleReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.document.RolePermissionEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.RoleReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.repository.RoleMongoRepository;

@ApplicationScoped
public class RoleMongoReadAdapter implements RoleReadPort {

    private final RoleMongoRepository roleMongoRepository;

    public RoleMongoReadAdapter(RoleMongoRepository roleMongoRepository) {
        this.roleMongoRepository = roleMongoRepository;
    }

    @Override
    public Optional<RoleReadModel> findById(UUID id) {
        return roleMongoRepository.findByIdOptional(id).map(this::toReadModel);
    }

    @Override
    public Optional<RoleReadModel> findByType(String type) {
        return roleMongoRepository.find("type", type).firstResultOptional().map(this::toReadModel);
    }

    private RoleReadModel toReadModel(RoleReadDocument document) {
        RoleReadModel readModel = new RoleReadModel();
        readModel.setId(document.getId());
        readModel.setName(document.getName());
        readModel.setType(document.getType());
        readModel.setDescription(document.getDescription());
        readModel.setPermissions(toPermissionReadModels(document.getPermissions()));
        readModel.setCreatedAt(document.getCreatedAt());
        readModel.setUpdatedAt(document.getUpdatedAt());
        return readModel;
    }

    private List<RolePermissionReadModel> toPermissionReadModels(List<RolePermissionEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toPermissionReadModel).toList();
    }

    private RolePermissionReadModel toPermissionReadModel(RolePermissionEmbed embed) {
        RolePermissionReadModel readModel = new RolePermissionReadModel();
        readModel.setId(embed.getId());
        readModel.setName(embed.getName());
        readModel.setDescription(embed.getDescription());
        readModel.setModule(embed.getModule());
        return readModel;
    }
}
