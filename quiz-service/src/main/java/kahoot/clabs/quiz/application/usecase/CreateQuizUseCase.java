package kahoot.clabs.quiz.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.command.CreateQuizCommand;
import kahoot.clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.repository.QuizRepository;

@ApplicationScoped
public class CreateQuizUseCase {

    private final QuizRepository quizRepository;

    @Inject
    public CreateQuizUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public QuizResponse execute(UUID organizationId, CreateQuizCommand command) {
        Quiz quiz = Quiz.create(organizationId, command.title(), command.createdById());
        return QuizResponse.from(quizRepository.save(quiz));
    }
}
