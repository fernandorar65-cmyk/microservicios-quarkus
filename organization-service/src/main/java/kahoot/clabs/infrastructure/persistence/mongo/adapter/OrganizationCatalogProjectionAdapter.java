package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import kahoot.clabs.application.event.CatalogItemProjectionSnapshot;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationDepartmentReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationJobReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationMemberStatusReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationStatusReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationDepartmentMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationJobMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationMemberStatusMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationStatusMongoRepository;

/**
 * Mongo catalog projection. Always outside JPA/JTA ({@link TxType#NOT_SUPPORTED}).
 */
@ApplicationScoped
public class OrganizationCatalogProjectionAdapter {

    private final OrganizationStatusMongoRepository statusMongoRepository;
    private final OrganizationMemberStatusMongoRepository memberStatusMongoRepository;
    private final OrganizationDepartmentMongoRepository departmentMongoRepository;
    private final OrganizationJobMongoRepository jobMongoRepository;

    @Inject
    public OrganizationCatalogProjectionAdapter(
            OrganizationStatusMongoRepository statusMongoRepository,
            OrganizationMemberStatusMongoRepository memberStatusMongoRepository,
            OrganizationDepartmentMongoRepository departmentMongoRepository,
            OrganizationJobMongoRepository jobMongoRepository) {
        this.statusMongoRepository = statusMongoRepository;
        this.memberStatusMongoRepository = memberStatusMongoRepository;
        this.departmentMongoRepository = departmentMongoRepository;
        this.jobMongoRepository = jobMongoRepository;
    }

    @Transactional(TxType.NOT_SUPPORTED)
    public void upsert(CatalogItemProjectionSnapshot payload) {
        if (payload == null || payload.catalogKind() == null) {
            return;
        }
        UUID id = payload.id();
        String name = payload.name();
        String description = payload.description();
        switch (payload.catalogKind()) {
            case CatalogItemProjectionSnapshot.KIND_STATUS -> {
                OrganizationStatusReadDocument document = new OrganizationStatusReadDocument();
                document.setId(id);
                document.setName(name);
                document.setDescription(description);
                statusMongoRepository.persistOrUpdate(document);
            }
            case CatalogItemProjectionSnapshot.KIND_MEMBER_STATUS -> {
                OrganizationMemberStatusReadDocument document = new OrganizationMemberStatusReadDocument();
                document.setId(id);
                document.setName(name);
                document.setDescription(description);
                memberStatusMongoRepository.persistOrUpdate(document);
            }
            case CatalogItemProjectionSnapshot.KIND_DEPARTMENT -> {
                OrganizationDepartmentReadDocument document = new OrganizationDepartmentReadDocument();
                document.setId(id);
                document.setName(name);
                document.setDescription(description);
                departmentMongoRepository.persistOrUpdate(document);
            }
            case CatalogItemProjectionSnapshot.KIND_JOB -> {
                OrganizationJobReadDocument document = new OrganizationJobReadDocument();
                document.setId(id);
                document.setName(name);
                document.setDescription(description);
                jobMongoRepository.persistOrUpdate(document);
            }
            default -> {
                // unknown kind ignored by caller logging
            }
        }
    }
}
