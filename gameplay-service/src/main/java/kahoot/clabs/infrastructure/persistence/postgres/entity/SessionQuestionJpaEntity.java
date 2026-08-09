package kahoot.clabs.gameplay.infrastructure.persistence.postgres.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import kahoot.clabs.gameplay.infrastructure.persistence.postgres.enums.QuestionTypeJpa;
import kahoot.clabs.gameplay.infrastructure.persistence.postgres.enums.SessionQuestionStatusJpa;

@Entity
@Table(
        name = "session_questions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_session_questions_session_position",
                columnNames = {"session_id", "position"}))
public class SessionQuestionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSessionJpaEntity session;

    /** Conceptual reference to quiz-service question. No FK. */
    @Column(name = "source_question_id", nullable = false, updatable = false)
    private UUID sourceQuestionId;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private QuestionTypeJpa type;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Column(name = "points")
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionQuestionStatusJpa status;

    @Column(name = "opened_at")
    private Instant openedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "sessionQuestion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<SessionAnswerOptionJpaEntity> answerOptions = new ArrayList<>();

    protected SessionQuestionJpaEntity() {
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

    public UUID getSourceQuestionId() {
        return sourceQuestionId;
    }

    public void setSourceQuestionId(UUID sourceQuestionId) {
        this.sourceQuestionId = sourceQuestionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionTypeJpa getType() {
        return type;
    }

    public void setType(QuestionTypeJpa type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public SessionQuestionStatusJpa getStatus() {
        return status;
    }

    public void setStatus(SessionQuestionStatusJpa status) {
        this.status = status;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<SessionAnswerOptionJpaEntity> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<SessionAnswerOptionJpaEntity> answerOptions) {
        this.answerOptions = answerOptions;
    }
}
