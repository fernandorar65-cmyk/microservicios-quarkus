package kahoot.clabs.application.port.out.read;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.application.readmodel.LeaderboardReadModel;

public interface LeaderboardReadPort {

    Optional<LeaderboardReadModel> findBySessionId(UUID sessionId);
}
