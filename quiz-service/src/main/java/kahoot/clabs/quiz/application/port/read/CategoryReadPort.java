package kahoot.clabs.quiz.application.port.read;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.quiz.application.readmodel.CategoryReadModel;

public interface CategoryReadPort {

    Optional<CategoryReadModel> findById(UUID id);

    List<CategoryReadModel> findByOrganization(UUID organizationId);
}
