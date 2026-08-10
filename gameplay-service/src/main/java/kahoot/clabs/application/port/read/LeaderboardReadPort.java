package kahoot.clabs.application.port.read;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.application.readmodel.LeaderboardReadModel;

public interface LeaderboardReadPort {

    Optional<LeaderboardReadModel> findBySessionId(UUID sessionId);
}
