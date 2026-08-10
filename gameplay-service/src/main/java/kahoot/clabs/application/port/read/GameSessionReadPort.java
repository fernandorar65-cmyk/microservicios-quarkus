package kahoot.clabs.application.port.read;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.application.readmodel.GameSessionReadModel;

public interface GameSessionReadPort {

    Optional<GameSessionReadModel> findById(UUID id);

    List<GameSessionReadModel> search(UUID organizationId, Collection<String> statuses, UUID quizId);
}
