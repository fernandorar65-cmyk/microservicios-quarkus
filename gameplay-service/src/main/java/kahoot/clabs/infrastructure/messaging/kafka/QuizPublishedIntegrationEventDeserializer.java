package kahoot.clabs.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class QuizPublishedIntegrationEventDeserializer
        extends ObjectMapperDeserializer<QuizPublishedIntegrationEvent> {

    public QuizPublishedIntegrationEventDeserializer() {
        super(QuizPublishedIntegrationEvent.class);
    }
}
