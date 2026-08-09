package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationMemberStatusJpaEntity;

@ApplicationScoped
public class OrganizationMemberStatusCatalogPanacheRepository
        implements PanacheRepositoryBase<OrganizationMemberStatusJpaEntity, UUID> {

    public boolean existsByName(String name) {
        return count("name", name) > 0;
    }
}
