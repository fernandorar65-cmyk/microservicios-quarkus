package kahoot.clabs.quiz.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.entity.Category;
import kahoot.clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.quiz.domain.shared.DomainException;

@ApplicationScoped
public class ManageQuizCategoriesUseCase {

    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;

    @Inject
    public ManageQuizCategoriesUseCase(QuizRepository quizRepository, CategoryRepository categoryRepository) {
        this.quizRepository = quizRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public QuizResponse assign(UUID organizationId, UUID quizId, UUID categoryId) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        Category category = requireOwnedCategory(organizationId, categoryId);
        quiz.assignCategory(category.getId());
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public void remove(UUID organizationId, UUID quizId, UUID categoryId) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        requireOwnedCategory(organizationId, categoryId);
        quiz.removeCategory(categoryId);
        quizRepository.save(quiz);
    }

    private Quiz requireOwnedQuiz(UUID organizationId, UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new DomainException("Quiz not found: " + quizId));
        if (!quiz.getOrganizationId().equals(organizationId)) {
            throw new DomainException("Quiz does not belong to the organization");
        }
        return quiz;
    }

    private Category requireOwnedCategory(UUID organizationId, UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainException("Category not found: " + categoryId));
        if (!category.getOrganizationId().equals(organizationId)) {
            throw new DomainException("Category does not belong to the organization");
        }
        return category;
    }
}
