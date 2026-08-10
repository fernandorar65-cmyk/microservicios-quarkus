package kahoot.clabs.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.UserCreatedEvent;
import kahoot.clabs.application.port.write.UserProjectionPort;
import kahoot.clabs.application.readmodel.UserReadModels;

@ApplicationScoped
public class UserCreatedConsumer {

    private static final Logger LOG = Logger.getLogger(UserCreatedConsumer.class);

    private final UserProjectionPort userProjectionPort;

    @Inject
    public UserCreatedConsumer(UserProjectionPort userProjectionPort) {
        this.userProjectionPort = userProjectionPort;
    }

    @Incoming("user-created-in")
    public void consume(UserCreatedEvent event) {
        if (event == null || event.userId() == null) {
            LOG.warn("Ignoring empty UserCreated message");
            return;
        }

        userProjectionPort.save(UserReadModels.from(event));
        LOG.infof("Projected UserCreated userId=%s email=%s", event.userId(), event.email());
    }
}
