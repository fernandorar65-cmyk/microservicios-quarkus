package kahoot.clabs.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.UserCreatedEvent;
import kahoot.clabs.application.port.integration.UserEventPublisher;

@ApplicationScoped
public class KafkaUserEventPublisher implements UserEventPublisher {

    private static final Logger LOG = Logger.getLogger(KafkaUserEventPublisher.class);

    private final Emitter<Record<String, UserCreatedEvent>> emitter;

    @Inject
    public KafkaUserEventPublisher(
            @Channel("user-created-out") Emitter<Record<String, UserCreatedEvent>> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void publish(UserCreatedEvent event) {
        LOG.infof(
                "Publishing UserCreated to identity.user.created userId=%s email=%s",
                event.userId(),
                event.email());
        emitter.send(Record.of(event.userId().toString(), event));
    }
}
