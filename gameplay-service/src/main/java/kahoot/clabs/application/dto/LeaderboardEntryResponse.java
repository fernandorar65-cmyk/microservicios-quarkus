package kahoot.clabs.application.dto;

import java.util.List;
import java.util.UUID;

import kahoot.clabs.application.readmodel.LeaderboardReadModel;

public record LeaderboardEntryResponse(
        int rank,
        UUID playerId,
        UUID userId,
        String nickname,
        int score,
        boolean connected) {

    public static List<LeaderboardEntryResponse> from(LeaderboardReadModel readModel) {
        return readModel.getRanking().stream()
                .map(entry -> new LeaderboardEntryResponse(
                        entry.getPosition(),
                        entry.getSessionPlayerId(),
                        entry.getUserId(),
                        entry.getNickname(),
                        entry.getScore(),
                        true))
                .toList();
    }
}
