package kahoot.clabs.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.domain.shared.BaseEntity;
import kahoot.clabs.domain.shared.DomainException;

public class PlayerAnswer extends BaseEntity {

    private final UUID sessionQuestionId;
    private final UUID sessionPlayerId;
    private final UUID sessionAnswerOptionId;
    private final boolean correct;
    private final long responseTimeMs;
    private final int awardedPoints;
    private final LocalDateTime answeredAt;

    private PlayerAnswer(
            UUID id,
            UUID sessionQuestionId,
            UUID sessionPlayerId,
            UUID sessionAnswerOptionId,
            boolean correct,
            long responseTimeMs,
            int awardedPoints,
            LocalDateTime answeredAt) {
        super(id);
        if (sessionQuestionId == null) {
            throw new DomainException("Session question id is required");
        }
        if (sessionPlayerId == null) {
            throw new DomainException("Session player id is required");
        }
        if (responseTimeMs < 0) {
            throw new DomainException("Response time cannot be negative");
        }
        if (awardedPoints < 0) {
            throw new DomainException("Awarded points cannot be negative");
        }
        this.sessionQuestionId = sessionQuestionId;
        this.sessionPlayerId = sessionPlayerId;
        this.sessionAnswerOptionId = sessionAnswerOptionId;
        this.correct = correct;
        this.responseTimeMs = responseTimeMs;
        this.awardedPoints = awardedPoints;
        this.answeredAt = answeredAt != null ? answeredAt : LocalDateTime.now();
    }

    public static PlayerAnswer submit(
            UUID sessionQuestionId,
            UUID sessionPlayerId,
            UUID sessionAnswerOptionId,
            boolean correct,
            long responseTimeMs,
            int awardedPoints) {
        return new PlayerAnswer(
                null,
                sessionQuestionId,
                sessionPlayerId,
                sessionAnswerOptionId,
                correct,
                responseTimeMs,
                awardedPoints,
                LocalDateTime.now());
    }

    public static PlayerAnswer rehydrate(
            UUID id,
            UUID sessionQuestionId,
            UUID sessionPlayerId,
            UUID sessionAnswerOptionId,
            boolean correct,
            long responseTimeMs,
            int awardedPoints,
            LocalDateTime answeredAt) {
        return new PlayerAnswer(
                id,
                sessionQuestionId,
                sessionPlayerId,
                sessionAnswerOptionId,
                correct,
                responseTimeMs,
                awardedPoints,
                answeredAt);
    }

    public UUID getSessionQuestionId() {
        return sessionQuestionId;
    }

    public UUID getSessionPlayerId() {
        return sessionPlayerId;
    }

    public UUID getSessionAnswerOptionId() {
        return sessionAnswerOptionId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public int getAwardedPoints() {
        return awardedPoints;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }
}
