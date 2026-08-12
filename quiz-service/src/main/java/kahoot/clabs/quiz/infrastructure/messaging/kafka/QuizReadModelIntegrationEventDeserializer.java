package kahoot.clabs.quiz.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import kahoot.clabs.quiz.application.event.QuizReadModelIntegrationEvent;

public class QuizReadModelIntegrationEventDeserializer
        extends ObjectMapperDeserializer<QuizReadModelIntegrationEvent> {

    public QuizReadModelIntegrationEventDeserializer() {
        super(QuizReadModelIntegrationEvent.class);
    }
}
