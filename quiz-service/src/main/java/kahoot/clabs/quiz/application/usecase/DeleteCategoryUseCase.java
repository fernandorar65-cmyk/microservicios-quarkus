package kahoot.clabs.quiz.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.quiz.domain.shared.DomainException;

@ApplicationScoped
public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public DeleteCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void execute(UUID id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new DomainException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
