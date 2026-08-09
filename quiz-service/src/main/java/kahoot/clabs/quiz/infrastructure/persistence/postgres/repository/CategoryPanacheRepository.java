package kahoot.clabs.quiz.infrastructure.persistence.postgres.repository;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.CategoryJpaEntity;

/**
 * Write-side only. Category list/get queries belong on Mongo {@code CategoryReadPort}.
 */
@ApplicationScoped
public class CategoryPanacheRepository implements PanacheRepositoryBase<CategoryJpaEntity, UUID> {
}
