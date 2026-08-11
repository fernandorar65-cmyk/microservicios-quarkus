package kahoot.clabs.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.GameSessionIntegrationEvent;
import kahoot.clabs.application.port.integration.GameSessionEventPublisher;

@ApplicationScoped
public class KafkaGameSessionEventPublisher implements GameSessionEventPublisher {

    private static final Logger LOG = Logger.getLogger(KafkaGameSessionEventPublisher.class);

    private final Emitter<Record<String, GameSessionIntegrationEvent>> emitter;

    @Inject
    public KafkaGameSessionEventPublisher(
            @Channel("session-events-out") Emitter<Record<String, GameSessionIntegrationEvent>> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void publish(GameSessionIntegrationEvent event) {
        LOG.infof(
                "Publishing %s to gameplay.session.events sessionId=%s eventId=%s",
                event.eventType(),
                event.aggregateId(),
                event.eventId());
        emitter.send(Record.of(event.aggregateId().toString(), event));
    }
}
