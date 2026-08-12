package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import jakarta.inject.Inject;
import kahoot.clabs.application.port.write.RoleProjectionPort;
import kahoot.clabs.application.readmodel.RolePermissionReadModel;
import kahoot.clabs.application.readmodel.RoleReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.document.RolePermissionEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.RoleReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.repository.RoleMongoRepository;

@ApplicationScoped
public class RoleProjectionAdapter implements RoleProjectionPort {

    private final RoleMongoRepository repository;

    @Inject
    public RoleProjectionAdapter(RoleMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(TxType.NOT_SUPPORTED)
    public void save(RoleReadModel readModel) {
        RoleReadDocument document = new RoleReadDocument();
        document.setId(readModel.getId());
        document.setName(readModel.getName());
        document.setType(readModel.getType());
        document.setDescription(readModel.getDescription());
        document.setPermissions(toEmbeds(readModel.getPermissions()));
        document.setCreatedAt(readModel.getCreatedAt());
        document.setUpdatedAt(readModel.getUpdatedAt());
        repository.persistOrUpdate(document);
    }

    private List<RolePermissionEmbed> toEmbeds(List<RolePermissionReadModel> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }
        return permissions.stream().map(permission -> {
            RolePermissionEmbed embed = new RolePermissionEmbed();
            embed.setId(permission.getId());
            embed.setName(permission.getName());
            embed.setDescription(permission.getDescription());
            embed.setModule(permission.getModule());
            return embed;
        }).toList();
    }
}
