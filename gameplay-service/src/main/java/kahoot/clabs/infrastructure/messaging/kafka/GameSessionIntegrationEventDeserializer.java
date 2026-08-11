package kahoot.clabs.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import kahoot.clabs.application.event.GameSessionIntegrationEvent;

public class GameSessionIntegrationEventDeserializer
        extends ObjectMapperDeserializer<GameSessionIntegrationEvent> {

    public GameSessionIntegrationEventDeserializer() {
        super(GameSessionIntegrationEvent.class);
    }
}
