package kahoot.clabs.application.port.write;

import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.application.readmodel.LeaderboardReadModel;

/**
 * Synchronizes game-session read models after write-side changes.
 */
public interface GameSessionProjectionPort {

    void save(GameSessionReadModel session, LeaderboardReadModel leaderboard);
}
