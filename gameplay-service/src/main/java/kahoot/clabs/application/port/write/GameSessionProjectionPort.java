package kahoot.clabs.application.port.write;

import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.application.readmodel.LeaderboardReadModel;

public interface GameSessionProjectionPort {

    void save(GameSessionReadModel session, LeaderboardReadModel leaderboard);
}
