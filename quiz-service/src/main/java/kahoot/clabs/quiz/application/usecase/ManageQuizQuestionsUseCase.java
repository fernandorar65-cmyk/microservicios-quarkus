package kahoot.clabs.quiz.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.command.QuestionCommand;
import kahoot.clabs.quiz.application.command.ReorderQuestionsCommand;
import kahoot.clabs.quiz.application.command.UpdateQuestionCommand;
import kahoot.clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.entity.Question;
import kahoot.clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.quiz.domain.shared.DomainException;

@ApplicationScoped
public class ManageQuizQuestionsUseCase {

    private final QuizRepository quizRepository;

    @Inject
    public ManageQuizQuestionsUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public QuizResponse add(UUID organizationId, UUID quizId, QuestionCommand command) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        Question question = quiz.addQuestion(command.title(), command.type());
        quiz.updateQuestion(
                question.getId(),
                command.title(),
                command.description(),
                command.difficulty(),
                command.points(),
                command.timeLimitSeconds());
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public QuizResponse update(UUID organizationId, UUID quizId, UUID questionId, UpdateQuestionCommand command) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.updateQuestion(
                questionId,
                command.title(),
                command.description(),
                command.difficulty(),
                command.points(),
                command.timeLimitSeconds());
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public QuizResponse reorder(UUID organizationId, UUID quizId, ReorderQuestionsCommand command) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.reorderQuestions(command.questionIds());
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public void remove(UUID organizationId, UUID quizId, UUID questionId) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.removeQuestion(questionId);
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
}
