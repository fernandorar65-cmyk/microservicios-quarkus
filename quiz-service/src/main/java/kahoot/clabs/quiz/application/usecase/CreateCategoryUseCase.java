package kahoot.clabs.quiz.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.command.CreateCategoryCommand;
import kahoot.clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.quiz.domain.entity.Category;
import kahoot.clabs.quiz.domain.repository.CategoryRepository;

@ApplicationScoped
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public CreateCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryResponse execute(CreateCategoryCommand command) {
        Category category = Category.create(command.organizationId(), command.name());
        category.changeDescription(command.description());
        category.changeColor(command.color());
        category.changeIcon(command.icon());
        return CategoryResponse.from(categoryRepository.save(category));
    }
}
