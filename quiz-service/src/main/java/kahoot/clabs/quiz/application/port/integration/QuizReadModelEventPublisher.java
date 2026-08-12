package kahoot.clabs.quiz.application.port.integration;

import kahoot.clabs.quiz.application.event.QuizReadModelIntegrationEvent;

/**
 * Publishes quiz/category read-model events to Kafka (never writes Mongo).
 */
public interface QuizReadModelEventPublisher {

    void publish(QuizReadModelIntegrationEvent event);
}
