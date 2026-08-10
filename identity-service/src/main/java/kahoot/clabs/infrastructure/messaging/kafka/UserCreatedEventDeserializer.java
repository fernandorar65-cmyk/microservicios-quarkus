package kahoot.clabs.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import kahoot.clabs.application.event.UserCreatedEvent;

public class UserCreatedEventDeserializer extends ObjectMapperDeserializer<UserCreatedEvent> {

    public UserCreatedEventDeserializer() {
        super(UserCreatedEvent.class);
    }
}
