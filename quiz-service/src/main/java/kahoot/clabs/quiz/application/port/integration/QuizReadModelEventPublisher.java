package kahoot.clabs.quiz.application.port.integration;

import kahoot.clabs.quiz.application.event.QuizReadModelIntegrationEvent;

public interface QuizReadModelEventPublisher {

    void publish(QuizReadModelIntegrationEvent event);
}
