package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.postgres.entity.PermissionJpaEntity;

/**
 * Write-side only. Used to rehydrate Role permissions on commands / seed.
 */
@ApplicationScoped
public class PermissionPanacheRepository implements PanacheRepositoryBase<PermissionJpaEntity, UUID> {

    public Optional<PermissionJpaEntity> findByNameIgnoreCaseAndModuleIgnoreCase(String name, String module) {
        return find("lower(name) = lower(?1) and lower(module) = lower(?2)", name, module)
                .firstResultOptional();
    }

    public List<PermissionJpaEntity> findByRoleId(UUID roleId) {
        return getEntityManager()
                .createQuery(
                        """
                        select p
                        from RoleJpaEntity r
                        join r.permissions p
                        where r.id = :roleId
                        """,
                        PermissionJpaEntity.class)
                .setParameter("roleId", roleId)
                .getResultList();
    }
}
