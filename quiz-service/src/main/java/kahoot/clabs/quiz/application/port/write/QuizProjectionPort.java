package kahoot.clabs.quiz.application.port.write;

import java.util.UUID;

import kahoot.clabs.quiz.application.readmodel.QuizReadModel;

public interface QuizProjectionPort {

    void save(QuizReadModel readModel);

    void deleteById(UUID id);
}
