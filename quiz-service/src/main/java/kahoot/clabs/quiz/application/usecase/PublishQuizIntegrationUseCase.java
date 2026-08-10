package kahoot.clabs.quiz.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.port.out.QuizPublishedIntegrationPort;
import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.repository.QuizRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PublishQuizIntegrationUseCase {

    private static final Logger LOG = Logger.getLogger(PublishQuizIntegrationUseCase.class);

    private final QuizRepository quizRepository;
    private final QuizPublishedIntegrationPort quizPublishedIntegrationPort;

    @Inject
    public PublishQuizIntegrationUseCase(
            QuizRepository quizRepository,
            QuizPublishedIntegrationPort quizPublishedIntegrationPort) {
        this.quizRepository = quizRepository;
        this.quizPublishedIntegrationPort = quizPublishedIntegrationPort;
    }

    @Transactional
    public void execute(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            LOG.warnf("Skipping QuizPublished integration — quiz not found: %s", quizId);
            return;
        }
        quizPublishedIntegrationPort.publish(quiz);
    }
}
