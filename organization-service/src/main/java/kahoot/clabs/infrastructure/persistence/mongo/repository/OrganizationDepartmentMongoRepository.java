package kahoot.clabs.infrastructure.persistence.mongo.repository;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationDepartmentReadDocument;

@ApplicationScoped
public class OrganizationDepartmentMongoRepository
        implements PanacheMongoRepositoryBase<OrganizationDepartmentReadDocument, UUID> {
}
