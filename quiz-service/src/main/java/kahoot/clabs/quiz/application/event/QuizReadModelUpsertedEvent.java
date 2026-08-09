package kahoot.clabs.quiz.application.event;

import kahoot.clabs.quiz.application.readmodel.QuizReadModel;

public record QuizReadModelUpsertedEvent(QuizReadModel readModel) {
}
