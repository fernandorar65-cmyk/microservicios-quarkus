package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import jakarta.inject.Inject;
import kahoot.clabs.application.port.read.OrganizationReadPort;
import kahoot.clabs.application.readmodel.OrganizationReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.mapper.OrganizationReadMapper;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationMongoRepository;

@ApplicationScoped
public class OrganizationReadAdapter implements OrganizationReadPort {

    @Inject
    OrganizationMongoRepository repository;

    @Inject
    OrganizationReadMapper mapper;

    @Override
    @Transactional(TxType.NOT_SUPPORTED)
    public Optional<OrganizationReadModel> findById(UUID id) {
        return repository.findByIdOptional(id).map(mapper::toReadModel);
    }

    @Override
    @Transactional(TxType.NOT_SUPPORTED)
    public Optional<OrganizationReadModel> findBySlug(String slug) {
        return repository.findBySlug(slug).map(mapper::toReadModel);
    }
}
