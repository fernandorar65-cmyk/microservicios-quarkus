package kahoot.clabs.quiz.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.quiz.application.port.read.CategoryReadPort;
import kahoot.clabs.quiz.domain.shared.DomainException;

@ApplicationScoped
public class GetCategoryUseCase {

    private final CategoryReadPort categoryReadPort;

    @Inject
    public GetCategoryUseCase(CategoryReadPort categoryReadPort) {
        this.categoryReadPort = categoryReadPort;
    }

    public CategoryResponse execute(UUID id) {
        return categoryReadPort.findById(id)
                .map(CategoryResponse::from)
                .orElseThrow(() -> new DomainException("Category not found: " + id));
    }
}
