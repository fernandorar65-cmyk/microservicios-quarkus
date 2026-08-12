package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.Optional;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.postgres.entity.UserJpaEntity;

@ApplicationScoped
public class UserPanacheRepository implements PanacheRepositoryBase<UserJpaEntity, UUID> {


    public Optional<UserJpaEntity> findByEmailIgnoreCase(String email) {
        return find("lower(email) = lower(?1)", email).firstResultOptional();
    }

    public boolean existsByEmailIgnoreCase(String email) {
        return count("lower(email) = lower(?1)", email) > 0;
    }
}
