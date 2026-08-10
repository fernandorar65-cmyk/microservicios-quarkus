package kahoot.clabs.quiz.infrastructure.messaging.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.usecase.PublishQuizIntegrationUseCase;
import kahoot.clabs.quiz.domain.event.QuizPublishedEvent;
import kahoot.clabs.quiz.domain.shared.DomainEvent;

/**
 * After a successful write-side commit, maps {@link QuizPublishedEvent} to the Kafka integration contract.
 */
@ApplicationScoped
public class QuizPublishedIntegrationListener {

    private final PublishQuizIntegrationUseCase publishQuizIntegrationUseCase;

    @Inject
    public QuizPublishedIntegrationListener(PublishQuizIntegrationUseCase publishQuizIntegrationUseCase) {
        this.publishQuizIntegrationUseCase = publishQuizIntegrationUseCase;
    }

    void onDomainEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) DomainEvent event) {
        if (event instanceof QuizPublishedEvent published) {
            publishQuizIntegrationUseCase.execute(published.getQuizId());
        }
    }
}
