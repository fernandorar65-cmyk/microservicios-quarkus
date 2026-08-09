package kahoot.clabs.quiz.application.port.out;

import java.util.UUID;

import kahoot.clabs.quiz.application.readmodel.QuizReadModel;

public interface QuizProjectionPort {

    void save(QuizReadModel readModel);

    void deleteById(UUID id);
}
