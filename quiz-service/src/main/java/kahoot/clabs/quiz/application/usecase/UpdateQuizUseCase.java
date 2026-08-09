package kahoot.clabs.quiz.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.command.QuizSettingsCommand;
import kahoot.clabs.quiz.application.command.UpdateQuizCommand;
import kahoot.clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.quiz.domain.shared.DomainException;
import kahoot.clabs.quiz.domain.valueobject.EstimatedTime;
import kahoot.clabs.quiz.domain.valueobject.QuizSettings;

@ApplicationScoped
public class UpdateQuizUseCase {

    private final QuizRepository quizRepository;

    @Inject
    public UpdateQuizUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public QuizResponse execute(UUID organizationId, UUID quizId, UpdateQuizCommand command) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new DomainException("Quiz not found: " + quizId));
        if (!quiz.getOrganizationId().equals(organizationId)) {
            throw new DomainException("Quiz does not belong to the organization");
        }

        quiz.rename(command.title());
        quiz.changeDescription(command.description());
        quiz.changeDifficulty(command.difficulty());
        quiz.changeEstimatedTime(EstimatedTime.ofMinutes(command.estimatedTimeMinutes()));
        quiz.changeSettings(toDomain(command.settings()));

        return QuizResponse.from(quizRepository.save(quiz));
    }

    private QuizSettings toDomain(QuizSettingsCommand settings) {
        return QuizSettings.of(
                settings.randomQuestions(),
                settings.randomAnswers(),
                settings.showCorrectAnswer(),
                settings.showRanking(),
                settings.allowRetry(),
                settings.showTimer(),
                settings.musicEnabled());
    }
}
