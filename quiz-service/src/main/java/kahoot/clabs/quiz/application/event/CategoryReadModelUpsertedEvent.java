package kahoot.clabs.quiz.application.event;

import kahoot.clabs.quiz.application.readmodel.CategoryReadModel;

public record CategoryReadModelUpsertedEvent(CategoryReadModel readModel) {
}
