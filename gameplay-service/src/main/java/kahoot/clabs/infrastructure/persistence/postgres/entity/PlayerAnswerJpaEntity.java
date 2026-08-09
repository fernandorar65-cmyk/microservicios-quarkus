package kahoot.clabs.gameplay.infrastructure.persistence.postgres.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "player_answers",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_player_answers_player_question",
                columnNames = {"session_player_id", "session_question_id"}))
public class PlayerAnswerJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSessionJpaEntity session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_player_id", nullable = false)
    private SessionPlayerJpaEntity sessionPlayer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_question_id", nullable = false)
    private SessionQuestionJpaEntity sessionQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_answer_option_id")
    private SessionAnswerOptionJpaEntity sessionAnswerOption;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "points_awarded", nullable = false)
    private int pointsAwarded;

    @Column(name = "answered_at", nullable = false)
    private Instant answeredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PlayerAnswerJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public GameSessionJpaEntity getSession() {
        return session;
    }

    public void setSession(GameSessionJpaEntity session) {
        this.session = session;
    }

    public SessionPlayerJpaEntity getSessionPlayer() {
        return sessionPlayer;
    }

    public void setSessionPlayer(SessionPlayerJpaEntity sessionPlayer) {
        this.sessionPlayer = sessionPlayer;
    }

    public SessionQuestionJpaEntity getSessionQuestion() {
        return sessionQuestion;
    }

    public void setSessionQuestion(SessionQuestionJpaEntity sessionQuestion) {
        this.sessionQuestion = sessionQuestion;
    }

    public SessionAnswerOptionJpaEntity getSessionAnswerOption() {
        return sessionAnswerOption;
    }

    public void setSessionAnswerOption(SessionAnswerOptionJpaEntity sessionAnswerOption) {
        this.sessionAnswerOption = sessionAnswerOption;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    public Instant getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Instant answeredAt) {
        this.answeredAt = answeredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
