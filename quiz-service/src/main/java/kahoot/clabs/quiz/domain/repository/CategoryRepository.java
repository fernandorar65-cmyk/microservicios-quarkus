package kahoot.clabs.quiz.domain.repository;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.quiz.domain.entity.Category;

/**
 * Write-side port for Category (PostgreSQL).
 * List/get category queries belong on Mongo {@code CategoryReadPort}.
 */
public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(UUID id);

    void delete(Category category);

    void deleteById(UUID id);
}
