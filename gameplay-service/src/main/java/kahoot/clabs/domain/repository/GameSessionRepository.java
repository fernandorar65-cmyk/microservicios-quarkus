package kahoot.clabs.domain.repository;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.domain.aggregate.GameSession;

public interface GameSessionRepository {

    GameSession save(GameSession session);

    Optional<GameSession> findById(UUID id);

    boolean existsById(UUID id);
}
