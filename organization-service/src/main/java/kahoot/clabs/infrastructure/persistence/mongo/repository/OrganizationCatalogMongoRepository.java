package kahoot.clabs.infrastructure.persistence.mongo.repository;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationCatalogReadDocument;

@ApplicationScoped
public class OrganizationCatalogMongoRepository
        implements PanacheMongoRepositoryBase<OrganizationCatalogReadDocument, String> {

    private static final String CATALOG_ID = "catalog";

    public Optional<OrganizationCatalogReadDocument> findCatalog() {
        return findByIdOptional(CATALOG_ID);
    }
}
