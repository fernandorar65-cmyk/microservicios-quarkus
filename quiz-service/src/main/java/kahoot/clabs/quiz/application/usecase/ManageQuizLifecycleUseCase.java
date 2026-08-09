package kahoot.clabs.quiz.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.command.DuplicateQuizCommand;
import kahoot.clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.quiz.domain.shared.DomainException;

@ApplicationScoped
public class ManageQuizLifecycleUseCase {

    private final QuizRepository quizRepository;

    @Inject
    public ManageQuizLifecycleUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public QuizResponse publish(UUID organizationId, UUID quizId) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.publish();
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public QuizResponse archive(UUID organizationId, UUID quizId) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.archive();
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public QuizResponse duplicate(UUID organizationId, UUID quizId, DuplicateQuizCommand command) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        return QuizResponse.from(quizRepository.save(quiz.duplicate(command.createdById())));
    }

    private Quiz requireOwnedQuiz(UUID organizationId, UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new DomainException("Quiz not found: " + quizId));
        if (!quiz.getOrganizationId().equals(organizationId)) {
            throw new DomainException("Quiz does not belong to the organization");
        }
        return quiz;
    }
}
