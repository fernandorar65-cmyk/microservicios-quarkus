package kahoot.clabs.quiz.application.port.write;

import java.util.UUID;

import kahoot.clabs.quiz.application.readmodel.CategoryReadModel;

public interface CategoryProjectionPort {

    void save(CategoryReadModel readModel);

    void deleteById(UUID id);
}
