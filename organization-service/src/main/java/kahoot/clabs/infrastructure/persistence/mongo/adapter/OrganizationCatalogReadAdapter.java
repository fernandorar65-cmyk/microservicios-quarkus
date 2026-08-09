package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import kahoot.clabs.application.port.out.read.OrganizationCatalogReadPort;
import kahoot.clabs.application.readmodel.OrganizationCatalogReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.mapper.OrganizationReadMapper;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationCatalogMongoRepository;

@ApplicationScoped
public class OrganizationCatalogReadAdapter implements OrganizationCatalogReadPort {

    @Inject
    OrganizationCatalogMongoRepository repository;

    @Inject
    OrganizationReadMapper mapper;

    @Override
    public Optional<OrganizationCatalogReadModel> findCatalog() {
        return repository.findCatalog().map(mapper::toReadModel);
    }
}
