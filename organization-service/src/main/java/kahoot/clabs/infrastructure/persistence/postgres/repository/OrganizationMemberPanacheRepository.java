package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationMemberJpaEntity;

@ApplicationScoped
public class OrganizationMemberPanacheRepository
        implements PanacheRepositoryBase<OrganizationMemberJpaEntity, UUID> {


    public List<OrganizationMemberJpaEntity> findByOrganizationId(UUID organizationId) {
        return list("organization.id", organizationId);
    }

    public void deleteByOrganizationId(UUID organizationId) {
        delete("organization.id", organizationId);
    }

    public void deleteByOrganizationIdAndIdNotIn(UUID organizationId, Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            deleteByOrganizationId(organizationId);
            return;
        }
        delete("organization.id = ?1 and id not in ?2", organizationId, ids);
    }
}
