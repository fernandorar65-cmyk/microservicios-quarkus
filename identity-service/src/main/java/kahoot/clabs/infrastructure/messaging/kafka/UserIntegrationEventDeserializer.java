package kahoot.clabs.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import kahoot.clabs.application.event.UserIntegrationEvent;

public class UserIntegrationEventDeserializer extends ObjectMapperDeserializer<UserIntegrationEvent> {

    public UserIntegrationEventDeserializer() {
        super(UserIntegrationEvent.class);
    }
}
