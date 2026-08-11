package kahoot.clabs.application.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.entity.PlayerAnswer;
import kahoot.clabs.domain.entity.SessionAnswerOption;
import kahoot.clabs.domain.entity.SessionPlayer;
import kahoot.clabs.domain.entity.SessionQuestion;

/**
 * Snapshot of a game session for Mongo read-model upsert (no secrets).
 */
public record GameSessionProjectionSnapshot(
        UUID sessionId,
        UUID organizationId,
        UUID quizId,
        UUID hostUserId,
        QuizPayload quiz,
        HostPayload host,
        String status,
        int currentQuestionIndex,
        List<PlayerPayload> players,
        List<QuestionPayload> questions,
        List<AnswerPayload> playerAnswers,
        int playerCount,
        Instant startedAt,
        Instant finishedAt,
        Instant createdAt,
        Instant updatedAt
) {

    public record QuizPayload(UUID id, String title, String thumbnailUrl) {
    }

    public record HostPayload(UUID id, String name) {
    }

    public record PlayerPayload(
            UUID id,
            UUID userId,
            String nickname,
            int score,
            boolean connected,
            Instant joinedAt,
            Instant leftAt
    ) {
    }

    public record QuestionPayload(
            UUID id,
            UUID sourceQuestionId,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            String title,
            String description,
            String questionType,
            Instant openedAt,
            Instant closedAt,
            List<AnswerOptionPayload> answerOptions
    ) {
    }

    public record AnswerOptionPayload(
            UUID id,
            UUID sourceAnswerOptionId,
            String text,
            boolean correct,
            int orderIndex
    ) {
    }

    public record AnswerPayload(
            UUID id,
            UUID sessionQuestionId,
            UUID sessionPlayerId,
            UUID sessionAnswerOptionId,
            boolean correct,
            long responseTimeMs,
            int awardedPoints,
            Instant answeredAt
    ) {
    }

    public record LeaderboardEntryPayload(
            int position,
            UUID sessionPlayerId,
            UUID userId,
            String nickname,
            int score
    ) {
    }

    public static GameSessionProjectionSnapshot from(GameSession session) {
        return from(session, null, null);
    }

    public static GameSessionProjectionSnapshot from(GameSession session, String quizTitle, String hostName) {
        List<PlayerPayload> players = session.getPlayers().stream()
                .map(GameSessionProjectionSnapshot::toPlayer)
                .toList();
        return new GameSessionProjectionSnapshot(
                session.getId(),
                session.getOrganizationId(),
                session.getQuizId(),
                session.getHostUserId(),
                new QuizPayload(session.getQuizId(), quizTitle, null),
                new HostPayload(session.getHostUserId(), hostName),
                session.getStatus().name(),
                session.getCurrentQuestionIndex(),
                players,
                session.getQuestions().stream().map(GameSessionProjectionSnapshot::toQuestion).toList(),
                session.getAnswers().stream().map(GameSessionProjectionSnapshot::toAnswer).toList(),
                players.size(),
                toInstant(session.getStartedAt()),
                toInstant(session.getFinishedAt()),
                toInstant(session.getCreatedAt()),
                toInstant(session.getUpdatedAt()));
    }

    public List<LeaderboardEntryPayload> leaderboardRanking() {
        AtomicInteger position = new AtomicInteger(1);
        return players.stream()
                .sorted((a, b) -> Integer.compare(b.score(), a.score()))
                .map(player -> new LeaderboardEntryPayload(
                        position.getAndIncrement(),
                        player.id(),
                        player.userId(),
                        player.nickname(),
                        player.score()))
                .toList();
    }

    private static PlayerPayload toPlayer(SessionPlayer player) {
        return new PlayerPayload(
                player.getId(),
                player.getUserId(),
                player.getNickname().value(),
                player.getScore(),
                player.isConnected(),
                toInstant(player.getJoinedAt()),
                toInstant(player.getLeftAt()));
    }

    private static QuestionPayload toQuestion(SessionQuestion question) {
        return new QuestionPayload(
                question.getId(),
                question.getSourceQuestionId(),
                question.getOrderIndex(),
                question.getPoints(),
                question.getTimeLimitSeconds(),
                question.getTitle(),
                question.getDescription(),
                question.getQuestionType(),
                toInstant(question.getOpenedAt()),
                toInstant(question.getClosedAt()),
                question.getOptions().stream().map(GameSessionProjectionSnapshot::toOption).toList());
    }

    private static AnswerOptionPayload toOption(SessionAnswerOption option) {
        return new AnswerOptionPayload(
                option.getId(),
                option.getSourceAnswerOptionId(),
                option.getText(),
                option.isCorrect(),
                option.getOrderIndex());
    }

    private static AnswerPayload toAnswer(PlayerAnswer answer) {
        return new AnswerPayload(
                answer.getId(),
                answer.getSessionQuestionId(),
                answer.getSessionPlayerId(),
                answer.getSessionAnswerOptionId(),
                answer.isCorrect(),
                answer.getResponseTimeMs(),
                answer.getAwardedPoints(),
                toInstant(answer.getAnsweredAt()));
    }

    private static Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
