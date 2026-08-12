package kahoot.clabs.quiz.infrastructure.event;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.event.CategoryReadModelDeletedEvent;
import kahoot.clabs.quiz.application.event.CategoryReadModelUpsertedEvent;
import kahoot.clabs.quiz.application.event.QuizReadModelDeletedEvent;
import kahoot.clabs.quiz.application.event.QuizReadModelIntegrationEvent;
import kahoot.clabs.quiz.application.event.QuizReadModelUpsertedEvent;
import kahoot.clabs.quiz.application.port.integration.QuizReadModelEventPublisher;

/**
 * After JPA commit, publishes Kafka events for Mongo projection (never writes Mongo here).
 */
@ApplicationScoped
public class ReadModelProjectionListener {

    private static final Logger log = Logger.getLogger(ReadModelProjectionListener.class);

    private final QuizReadModelEventPublisher quizReadModelEventPublisher;

    @Inject
    public ReadModelProjectionListener(QuizReadModelEventPublisher quizReadModelEventPublisher) {
        this.quizReadModelEventPublisher = quizReadModelEventPublisher;
    }

    void onQuizUpserted(@Observes(during = TransactionPhase.AFTER_SUCCESS) QuizReadModelUpsertedEvent event) {
        log.infof("publishing quiz upsert aggregateId=%s", event.readModel().getId());
        quizReadModelEventPublisher.publish(QuizReadModelIntegrationEvent.quizUpserted(event.readModel()));
    }

    void onQuizDeleted(@Observes(during = TransactionPhase.AFTER_SUCCESS) QuizReadModelDeletedEvent event) {
        log.infof("publishing quiz delete aggregateId=%s", event.quizId());
        quizReadModelEventPublisher.publish(QuizReadModelIntegrationEvent.quizDeleted(event.quizId()));
    }

    void onCategoryUpserted(@Observes(during = TransactionPhase.AFTER_SUCCESS) CategoryReadModelUpsertedEvent event) {
        log.infof("publishing category upsert aggregateId=%s", event.readModel().getId());
        quizReadModelEventPublisher.publish(QuizReadModelIntegrationEvent.categoryUpserted(event.readModel()));
    }

    void onCategoryDeleted(@Observes(during = TransactionPhase.AFTER_SUCCESS) CategoryReadModelDeletedEvent event) {
        log.infof("publishing category delete aggregateId=%s", event.categoryId());
        quizReadModelEventPublisher.publish(QuizReadModelIntegrationEvent.categoryDeleted(event.categoryId()));
    }
}
