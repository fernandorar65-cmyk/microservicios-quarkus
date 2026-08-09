package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.Optional;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationJpaEntity;

/**
 * Write-side only. Query/list reads belong on Mongo {@code OrganizationReadPort}.
 */
@ApplicationScoped
public class OrganizationPanacheRepository implements PanacheRepositoryBase<OrganizationJpaEntity, UUID> {

    /** Command rehydration / uniqueness. */
    public Optional<OrganizationJpaEntity> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }

    public boolean existsBySlug(String slug) {
        return count("slug", slug) > 0;
    }
}
