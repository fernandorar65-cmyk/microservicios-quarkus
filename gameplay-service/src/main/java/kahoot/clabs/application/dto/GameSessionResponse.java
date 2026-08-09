package kahoot.clabs.application.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.domain.aggregate.GameSession;

public record GameSessionResponse(
        UUID id,
        UUID organizationId,
        UUID quizId,
        UUID hostUserId,
        String status,
        int currentQuestionIndex,
        int playerCount,
        int questionCount,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static GameSessionResponse from(GameSession session) {
        return new GameSessionResponse(
                session.getId(),
                session.getOrganizationId(),
                session.getQuizId(),
                session.getHostUserId(),
                session.getStatus().name(),
                session.getCurrentQuestionIndex(),
                session.getPlayers().size(),
                session.getQuestions().size(),
                session.getStartedAt(),
                session.getFinishedAt(),
                session.getCreatedAt(),
                session.getUpdatedAt());
    }

    public static GameSessionResponse from(GameSessionReadModel readModel) {
        return new GameSessionResponse(
                readModel.getId(),
                readModel.getOrganizationId(),
                readModel.getQuizId(),
                readModel.getHostUserId(),
                readModel.getStatus(),
                readModel.getCurrentQuestionIndex(),
                readModel.getPlayerCount() > 0
                        ? readModel.getPlayerCount()
                        : readModel.getPlayers().size(),
                readModel.getQuestions().size(),
                toLocalDateTime(readModel.getStartedAt()),
                toLocalDateTime(readModel.getFinishedAt()),
                toLocalDateTime(readModel.getCreatedAt()),
                toLocalDateTime(readModel.getUpdatedAt()));
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
