package kahoot.clabs.quiz.infrastructure.persistence.postgres.entity;

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

import kahoot.clabs.quiz.infrastructure.persistence.postgres.enums.QuestionTypeJpa;

/**
 * JPA persistence model for questions. Owned by Quiz aggregate.
 */
@Entity
@Table(
        name = "questions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_questions_quiz_position",
                columnNames = {"quiz_id", "position"}))
public class QuestionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizJpaEntity quiz;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<AnswerOptionJpaEntity> answerOptions = new ArrayList<>();

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<QuestionAssetJpaEntity> assets = new ArrayList<>();

    protected QuestionJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public QuizJpaEntity getQuiz() {
        return quiz;
    }

    public void setQuiz(QuizJpaEntity quiz) {
        this.quiz = quiz;
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

    public List<AnswerOptionJpaEntity> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<AnswerOptionJpaEntity> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public List<QuestionAssetJpaEntity> getAssets() {
        return assets;
    }

    public void setAssets(List<QuestionAssetJpaEntity> assets) {
        this.assets = assets;
    }
}
