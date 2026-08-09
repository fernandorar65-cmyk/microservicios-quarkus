package kahoot.clabs.quiz.infrastructure.persistence.postgres.adapter;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.event.CategoryReadModelDeletedEvent;
import kahoot.clabs.quiz.application.event.CategoryReadModelUpsertedEvent;
import kahoot.clabs.quiz.application.readmodel.CategoryReadModels;
import kahoot.clabs.quiz.domain.entity.Category;
import kahoot.clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.mapper.CategoryMapper;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.repository.CategoryPanacheRepository;

@ApplicationScoped
public class JpaCategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryPanacheRepository categoryPanacheRepository;
    private final Event<CategoryReadModelUpsertedEvent> categoryReadModelUpsertedEvent;
    private final Event<CategoryReadModelDeletedEvent> categoryReadModelDeletedEvent;

    @Inject
    public JpaCategoryRepositoryAdapter(
            CategoryPanacheRepository categoryPanacheRepository,
            Event<CategoryReadModelUpsertedEvent> categoryReadModelUpsertedEvent,
            Event<CategoryReadModelDeletedEvent> categoryReadModelDeletedEvent) {
        this.categoryPanacheRepository = categoryPanacheRepository;
        this.categoryReadModelUpsertedEvent = categoryReadModelUpsertedEvent;
        this.categoryReadModelDeletedEvent = categoryReadModelDeletedEvent;
    }

    @Override
    @Transactional
    public Category save(Category category) {
        Category saved = CategoryMapper.toDomain(
                categoryPanacheRepository.getEntityManager().merge(CategoryMapper.toEntity(category)));
        categoryReadModelUpsertedEvent.fire(new CategoryReadModelUpsertedEvent(CategoryReadModels.from(saved)));
        return saved;
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryPanacheRepository.findByIdOptional(id).map(CategoryMapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(Category category) {
        categoryPanacheRepository.deleteById(category.getId());
        categoryReadModelDeletedEvent.fire(new CategoryReadModelDeletedEvent(category.getId()));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        categoryPanacheRepository.deleteById(id);
        categoryReadModelDeletedEvent.fire(new CategoryReadModelDeletedEvent(id));
    }
}
