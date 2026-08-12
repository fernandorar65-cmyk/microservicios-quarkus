package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.port.write.PermissionProjectionPort;
import kahoot.clabs.application.readmodel.PermissionReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.document.PermissionReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.repository.PermissionMongoRepository;

@ApplicationScoped
public class PermissionProjectionAdapter implements PermissionProjectionPort {

    private final PermissionMongoRepository repository;

    @Inject
    public PermissionProjectionAdapter(PermissionMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(PermissionReadModel readModel) {
        PermissionReadDocument document = new PermissionReadDocument();
        document.setId(readModel.getId());
        document.setName(readModel.getName());
        document.setDescription(readModel.getDescription());
        document.setModule(readModel.getModule());
        document.setCreatedAt(readModel.getCreatedAt());
        document.setUpdatedAt(readModel.getUpdatedAt());
        repository.persistOrUpdate(document);
    }
}
