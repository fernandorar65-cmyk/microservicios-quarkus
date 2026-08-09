package kahoot.clabs.quiz.application.port.out.read;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.quiz.application.readmodel.QuizReadModel;

public interface QuizReadPort {

    Optional<QuizReadModel> findById(UUID id);

    List<QuizReadModel> findByOrganization(UUID organizationId);
}
