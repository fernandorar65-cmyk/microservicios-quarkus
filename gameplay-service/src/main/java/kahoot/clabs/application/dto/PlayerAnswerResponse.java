package kahoot.clabs.application.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.domain.entity.PlayerAnswer;

public record PlayerAnswerResponse(
        UUID id,
        UUID sessionQuestionId,
        UUID sessionPlayerId,
        UUID sessionAnswerOptionId,
        boolean correct,
        long responseTimeMs,
        int awardedPoints,
        LocalDateTime answeredAt) {

    public static PlayerAnswerResponse from(PlayerAnswer answer) {
        return new PlayerAnswerResponse(
                answer.getId(),
                answer.getSessionQuestionId(),
                answer.getSessionPlayerId(),
                answer.getSessionAnswerOptionId(),
                answer.isCorrect(),
                answer.getResponseTimeMs(),
                answer.getAwardedPoints(),
                answer.getAnsweredAt());
    }

    public static PlayerAnswerResponse from(GameSessionReadModel.PlayerAnswer answer) {
        return new PlayerAnswerResponse(
                answer.getId(),
                answer.getSessionQuestionId(),
                answer.getSessionPlayerId(),
                answer.getSessionAnswerOptionId(),
                answer.isCorrect(),
                answer.getResponseTimeMs(),
                answer.getAwardedPoints(),
                toLocalDateTime(answer.getAnsweredAt()));
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
