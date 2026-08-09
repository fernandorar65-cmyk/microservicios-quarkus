package kahoot.clabs.infrastructure.persistence.mongo.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationReadDocument;

@ApplicationScoped
public class OrganizationMongoRepository
        implements PanacheMongoRepositoryBase<OrganizationReadDocument, UUID> {

    public Optional<OrganizationReadDocument> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }
}
