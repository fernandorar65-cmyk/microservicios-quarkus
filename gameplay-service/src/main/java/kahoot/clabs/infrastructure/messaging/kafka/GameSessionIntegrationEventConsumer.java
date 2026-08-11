package kahoot.clabs.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.GameSessionIntegrationEvent;
import kahoot.clabs.application.port.write.GameSessionProjectionPort;
import kahoot.clabs.application.readmodel.GameSessionReadModels;

@ApplicationScoped
public class GameSessionIntegrationEventConsumer {

    private static final Logger LOG = Logger.getLogger(GameSessionIntegrationEventConsumer.class);

    private final GameSessionProjectionPort gameSessionProjectionPort;

    @Inject
    public GameSessionIntegrationEventConsumer(GameSessionProjectionPort gameSessionProjectionPort) {
        this.gameSessionProjectionPort = gameSessionProjectionPort;
    }

    @Incoming("session-events-in")
    public void consume(GameSessionIntegrationEvent event) {
        if (event == null || event.payload() == null || event.aggregateId() == null) {
            LOG.warn("Ignoring empty game-session integration message");
            return;
        }

        gameSessionProjectionPort.save(
                GameSessionReadModels.from(event.payload()),
                GameSessionReadModels.toLeaderboard(event.payload()));
        LOG.infof(
                "Projected %s sessionId=%s eventId=%s",
                event.eventType(),
                event.aggregateId(),
                event.eventId());
    }
}
