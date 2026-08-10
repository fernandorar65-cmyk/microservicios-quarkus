package kahoot.clabs.quiz.application.port.write;

import kahoot.clabs.quiz.domain.aggregate.Quiz;

/**
 * Publishes the integration fact that a quiz was published (Kafka / other brokers).
 */
public interface QuizPublishedIntegrationPort {

    void publish(Quiz quiz);
}
