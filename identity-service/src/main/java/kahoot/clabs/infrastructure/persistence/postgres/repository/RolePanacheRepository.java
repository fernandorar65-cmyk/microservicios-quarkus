package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.Optional;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.postgres.entity.RoleJpaEntity;

@ApplicationScoped
public class RolePanacheRepository implements PanacheRepositoryBase<RoleJpaEntity, UUID> {


    public Optional<RoleJpaEntity> findByType(String type) {
        return find("type", type).firstResultOptional();
    }
}
