package kahoot.clabs.application.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.domain.entity.SessionPlayer;

public record SessionPlayerResponse(
        UUID id,
        UUID userId,
        String nickname,
        int score,
        boolean connected,
        LocalDateTime joinedAt,
        LocalDateTime leftAt) {

    public static SessionPlayerResponse from(SessionPlayer player) {
        return new SessionPlayerResponse(
                player.getId(),
                player.getUserId(),
                player.getNickname().value(),
                player.getScore(),
                player.isConnected(),
                player.getJoinedAt(),
                player.getLeftAt());
    }

    public static SessionPlayerResponse from(GameSessionReadModel.SessionPlayer player) {
        return new SessionPlayerResponse(
                player.getId(),
                player.getUserId(),
                player.getNickname(),
                player.getScore(),
                player.isConnected(),
                toLocalDateTime(player.getJoinedAt()),
                toLocalDateTime(player.getLeftAt()));
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
