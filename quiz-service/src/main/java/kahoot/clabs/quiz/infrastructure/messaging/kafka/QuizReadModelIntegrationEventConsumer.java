package kahoot.clabs.quiz.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.event.QuizReadModelIntegrationEvent;
import kahoot.clabs.quiz.application.port.write.CategoryProjectionPort;
import kahoot.clabs.quiz.application.port.write.QuizProjectionPort;

@ApplicationScoped
public class QuizReadModelIntegrationEventConsumer {

    private static final Logger LOG = Logger.getLogger(QuizReadModelIntegrationEventConsumer.class);

    private final QuizProjectionPort quizProjectionPort;
    private final CategoryProjectionPort categoryProjectionPort;

    @Inject
    public QuizReadModelIntegrationEventConsumer(
            QuizProjectionPort quizProjectionPort, CategoryProjectionPort categoryProjectionPort) {
        this.quizProjectionPort = quizProjectionPort;
        this.categoryProjectionPort = categoryProjectionPort;
    }

    @Incoming("quiz-read-events-in")
    public void consume(QuizReadModelIntegrationEvent event) {
        if (event == null || event.aggregateId() == null || event.eventType() == null) {
            LOG.warn("Ignoring empty quiz read-model message");
            return;
        }

        switch (event.eventType()) {
            case QuizReadModelIntegrationEvent.QUIZ_UPSERTED -> {
                if (event.quiz() == null) {
                    LOG.warn("Ignoring QuizUpserted without payload");
                    return;
                }
                quizProjectionPort.save(event.quiz());
            }
            case QuizReadModelIntegrationEvent.QUIZ_DELETED -> quizProjectionPort.deleteById(event.aggregateId());
            case QuizReadModelIntegrationEvent.CATEGORY_UPSERTED -> {
                if (event.category() == null) {
                    LOG.warn("Ignoring CategoryUpserted without payload");
                    return;
                }
                categoryProjectionPort.save(event.category());
            }
            case QuizReadModelIntegrationEvent.CATEGORY_DELETED ->
                    categoryProjectionPort.deleteById(event.aggregateId());
            default -> {
                LOG.warnf("Ignoring unknown quiz read eventType=%s", event.eventType());
                return;
            }
        }
        LOG.infof(
                "Projected %s aggregateId=%s eventId=%s",
                event.eventType(),
                event.aggregateId(),
                event.eventId());
    }
}
