package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationJobJpaEntity;

@ApplicationScoped
public class OrganizationJobPanacheRepository
        implements PanacheRepositoryBase<OrganizationJobJpaEntity, UUID> {

    public boolean existsByName(String name) {
        return count("name", name) > 0;
    }
}
