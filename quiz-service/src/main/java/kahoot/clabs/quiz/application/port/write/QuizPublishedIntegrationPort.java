package kahoot.clabs.quiz.application.port.write;

import kahoot.clabs.quiz.domain.aggregate.Quiz;

public interface QuizPublishedIntegrationPort {

    void publish(Quiz quiz);
}
