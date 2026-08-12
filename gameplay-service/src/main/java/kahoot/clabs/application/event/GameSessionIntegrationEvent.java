package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.UUID;

public record GameSessionIntegrationEvent(
        UUID eventId,
        String eventType,
        int version,
        Instant occurredAt,
        UUID aggregateId,
        GameSessionProjectionSnapshot payload
) {

    public static final int VERSION = 1;

    public static final String SESSION_CREATED = "SessionCreated";
    public static final String SESSION_STARTED = "SessionStarted";
    public static final String SESSION_CANCELLED = "SessionCancelled";
    public static final String SESSION_FINISHED = "SessionFinished";
    public static final String PLAYER_JOINED = "PlayerJoined";
    public static final String PLAYER_LEFT = "PlayerLeft";
    public static final String PLAYER_NICKNAME_UPDATED = "PlayerNicknameUpdated";
    public static final String QUESTION_OPENED = "QuestionOpened";
    public static final String QUESTION_CLOSED = "QuestionClosed";
    public static final String QUESTION_ADVANCED = "QuestionAdvanced";
    public static final String ANSWER_SUBMITTED = "AnswerSubmitted";

    public static GameSessionIntegrationEvent of(String eventType, GameSessionProjectionSnapshot payload) {
        return new GameSessionIntegrationEvent(
                UUID.randomUUID(),
                eventType,
                VERSION,
                Instant.now(),
                payload.sessionId(),
                payload);
    }

    public static GameSessionIntegrationEvent sessionCreated(GameSessionProjectionSnapshot payload) {
        return of(SESSION_CREATED, payload);
    }

    public static GameSessionIntegrationEvent sessionStarted(GameSessionProjectionSnapshot payload) {
        return of(SESSION_STARTED, payload);
    }

    public static GameSessionIntegrationEvent sessionCancelled(GameSessionProjectionSnapshot payload) {
        return of(SESSION_CANCELLED, payload);
    }

    public static GameSessionIntegrationEvent sessionFinished(GameSessionProjectionSnapshot payload) {
        return of(SESSION_FINISHED, payload);
    }

    public static GameSessionIntegrationEvent playerJoined(GameSessionProjectionSnapshot payload) {
        return of(PLAYER_JOINED, payload);
    }

    public static GameSessionIntegrationEvent playerLeft(GameSessionProjectionSnapshot payload) {
        return of(PLAYER_LEFT, payload);
    }

    public static GameSessionIntegrationEvent playerNicknameUpdated(GameSessionProjectionSnapshot payload) {
        return of(PLAYER_NICKNAME_UPDATED, payload);
    }

    public static GameSessionIntegrationEvent questionOpened(GameSessionProjectionSnapshot payload) {
        return of(QUESTION_OPENED, payload);
    }

    public static GameSessionIntegrationEvent questionClosed(GameSessionProjectionSnapshot payload) {
        return of(QUESTION_CLOSED, payload);
    }

    public static GameSessionIntegrationEvent questionAdvanced(GameSessionProjectionSnapshot payload) {
        return of(QUESTION_ADVANCED, payload);
    }

    public static GameSessionIntegrationEvent answerSubmitted(GameSessionProjectionSnapshot payload) {
        return of(ANSWER_SUBMITTED, payload);
    }
}
