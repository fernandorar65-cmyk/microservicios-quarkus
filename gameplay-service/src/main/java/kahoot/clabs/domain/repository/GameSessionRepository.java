package kahoot.clabs.domain.repository;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.domain.aggregate.GameSession;

/**
 * Write-side port for the GameSession aggregate (PostgreSQL).
 * Listings / leaderboard / get-session for queries belong on Mongo read ports.
 */
public interface GameSessionRepository {

    GameSession save(GameSession session);

    Optional<GameSession> findById(UUID id);

    boolean existsById(UUID id);
}
