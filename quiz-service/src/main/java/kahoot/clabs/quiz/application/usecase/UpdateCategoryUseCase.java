package kahoot.clabs.quiz.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.command.UpdateCategoryCommand;
import kahoot.clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.quiz.domain.entity.Category;
import kahoot.clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.quiz.domain.shared.DomainException;

@ApplicationScoped
public class UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public UpdateCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryResponse execute(UUID id, UpdateCategoryCommand command) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DomainException("Category not found: " + id));
        category.rename(command.name());
        category.changeDescription(command.description());
        category.changeColor(command.color());
        category.changeIcon(command.icon());
        return CategoryResponse.from(categoryRepository.save(category));
    }
}
