package kahoot.clabs.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.UserIntegrationEvent;
import kahoot.clabs.application.port.integration.UserEventPublisher;

@ApplicationScoped
public class KafkaUserEventPublisher implements UserEventPublisher {

    private static final Logger LOG = Logger.getLogger(KafkaUserEventPublisher.class);

    private final Emitter<Record<String, UserIntegrationEvent>> emitter;

    @Inject
    public KafkaUserEventPublisher(
            @Channel("user-events-out") Emitter<Record<String, UserIntegrationEvent>> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void publish(UserIntegrationEvent event) {
        LOG.infof(
                "Publishing %s to identity.user.events userId=%s eventId=%s",
                event.eventType(),
                event.aggregateId(),
                event.eventId());
        emitter.send(Record.of(event.aggregateId().toString(), event));
    }
}
