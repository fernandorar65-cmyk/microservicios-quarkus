package kahoot.clabs.quiz.infrastructure.persistence.postgres.repository;

import java.util.Optional;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.CategoryJpaEntity;

@ApplicationScoped
public class CategoryPanacheRepository implements PanacheRepositoryBase<CategoryJpaEntity, UUID> {

    public Optional<CategoryJpaEntity> findByOrganizationIdAndNameIgnoreCase(UUID organizationId, String name) {
        return find("organizationId = ?1 and lower(name) = lower(?2)", organizationId, name)
                .firstResultOptional();
    }
}
