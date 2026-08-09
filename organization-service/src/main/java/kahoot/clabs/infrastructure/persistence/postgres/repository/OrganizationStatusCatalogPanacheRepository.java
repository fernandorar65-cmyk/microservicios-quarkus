package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationStatusJpaEntity;

@ApplicationScoped
public class OrganizationStatusCatalogPanacheRepository
        implements PanacheRepositoryBase<OrganizationStatusJpaEntity, UUID> {

    public boolean existsByName(String name) {
        return count("name", name) > 0;
    }
}
