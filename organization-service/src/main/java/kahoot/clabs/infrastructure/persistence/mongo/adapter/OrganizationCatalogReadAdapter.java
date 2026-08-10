package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.port.read.OrganizationCatalogReadPort;
import kahoot.clabs.application.readmodel.CatalogItemReadModel;
import kahoot.clabs.application.readmodel.OrganizationCatalogReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationDepartmentReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationJobReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationMemberStatusReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationStatusReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationDepartmentMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationJobMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationMemberStatusMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationStatusMongoRepository;

@ApplicationScoped
public class OrganizationCatalogReadAdapter implements OrganizationCatalogReadPort {

    @Inject
    OrganizationDepartmentMongoRepository departmentRepository;

    @Inject
    OrganizationJobMongoRepository jobRepository;

    @Inject
    OrganizationStatusMongoRepository statusRepository;

    @Inject
    OrganizationMemberStatusMongoRepository memberStatusRepository;

    @Override
    public Optional<OrganizationCatalogReadModel> findCatalog() {
        OrganizationCatalogReadModel model = new OrganizationCatalogReadModel();
        model.setId("catalog");
        model.setDepartments(mapItems(departmentRepository.listAll(), this::toItem));
        model.setJobs(mapItems(jobRepository.listAll(), this::toItem));
        model.setOrganizationStatuses(mapItems(statusRepository.listAll(), this::toItem));
        model.setMemberStatuses(mapItems(memberStatusRepository.listAll(), this::toItem));
        return Optional.of(model);
    }

    private <T> List<CatalogItemReadModel> mapItems(List<T> source, Function<T, CatalogItemReadModel> mapper) {
        return source.stream().map(mapper).toList();
    }

    private CatalogItemReadModel toItem(OrganizationDepartmentReadDocument document) {
        return toItem(document.getId(), document.getName(), document.getDescription());
    }

    private CatalogItemReadModel toItem(OrganizationJobReadDocument document) {
        return toItem(document.getId(), document.getName(), document.getDescription());
    }

    private CatalogItemReadModel toItem(OrganizationStatusReadDocument document) {
        return toItem(document.getId(), document.getName(), document.getDescription());
    }

    private CatalogItemReadModel toItem(OrganizationMemberStatusReadDocument document) {
        return toItem(document.getId(), document.getName(), document.getDescription());
    }

    private CatalogItemReadModel toItem(java.util.UUID id, String name, String description) {
        CatalogItemReadModel model = new CatalogItemReadModel();
        model.setId(id);
        model.setName(name);
        model.setDescription(description);
        return model;
    }
}
