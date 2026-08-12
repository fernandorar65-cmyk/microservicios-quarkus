package kahoot.clabs.quiz.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.event.QuizReadModelIntegrationEvent;
import kahoot.clabs.quiz.application.port.integration.QuizReadModelEventPublisher;

@ApplicationScoped
public class KafkaQuizReadModelEventPublisher implements QuizReadModelEventPublisher {

    private static final Logger LOG = Logger.getLogger(KafkaQuizReadModelEventPublisher.class);

    private final Emitter<Record<String, QuizReadModelIntegrationEvent>> emitter;

    @Inject
    public KafkaQuizReadModelEventPublisher(
            @Channel("quiz-read-events-out") Emitter<Record<String, QuizReadModelIntegrationEvent>> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void publish(QuizReadModelIntegrationEvent event) {
        LOG.infof(
                "Publishing %s to quiz.read.events aggregateId=%s eventId=%s",
                event.eventType(),
                event.aggregateId(),
                event.eventId());
        emitter.send(Record.of(event.aggregateId().toString(), event));
    }
}
