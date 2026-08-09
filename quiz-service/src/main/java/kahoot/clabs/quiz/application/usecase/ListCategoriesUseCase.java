package kahoot.clabs.quiz.application.usecase;

import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.quiz.application.port.out.read.CategoryReadPort;

@ApplicationScoped
public class ListCategoriesUseCase {

    private final CategoryReadPort categoryReadPort;

    @Inject
    public ListCategoriesUseCase(CategoryReadPort categoryReadPort) {
        this.categoryReadPort = categoryReadPort;
    }

    public List<CategoryResponse> execute(UUID organizationId) {
        return categoryReadPort.findByOrganization(organizationId).stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
