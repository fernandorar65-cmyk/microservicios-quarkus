package kahoot.clabs.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.UserIntegrationEvent;
import kahoot.clabs.application.port.write.UserProjectionPort;
import kahoot.clabs.application.readmodel.UserReadModels;

@ApplicationScoped
public class UserIntegrationEventConsumer {

    private static final Logger LOG = Logger.getLogger(UserIntegrationEventConsumer.class);

    private final UserProjectionPort userProjectionPort;

    @Inject
    public UserIntegrationEventConsumer(UserProjectionPort userProjectionPort) {
        this.userProjectionPort = userProjectionPort;
    }

    @Incoming("user-events-in")
    public void consume(UserIntegrationEvent event) {
        if (event == null || event.payload() == null || event.aggregateId() == null) {
            LOG.warn("Ignoring empty user integration message");
            return;
        }

        userProjectionPort.save(UserReadModels.from(event.payload()));
        LOG.infof(
                "Projected %s userId=%s eventId=%s",
                event.eventType(),
                event.aggregateId(),
                event.eventId());
    }
}
