package kahoot.clabs.quiz.infrastructure.persistence.postgres.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import kahoot.clabs.quiz.infrastructure.persistence.postgres.enums.QuizDifficultyJpa;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.enums.QuizStatusJpa;

/**
 * JPA persistence model for quizzes. Not a domain Aggregate.
 */
@Entity
@Table(name = "quizzes")
public class QuizJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QuizStatusJpa status;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 20)
    private QuizDifficultyJpa difficulty;

    @Column(name = "estimated_time_minutes")
    private Integer estimatedTimeMinutes;

    @Column(name = "random_questions", nullable = false)
    private boolean randomQuestions;

    @Column(name = "random_answers", nullable = false)
    private boolean randomAnswers;

    @Column(name = "show_correct_answer", nullable = false)
    private boolean showCorrectAnswer;

    @Column(name = "show_ranking", nullable = false)
    private boolean showRanking;

    @Column(name = "allow_retry", nullable = false)
    private boolean allowRetry;

    @Column(name = "show_timer", nullable = false)
    private boolean showTimer;

    @Column(name = "music_enabled", nullable = false)
    private boolean musicEnabled;

    @Column(name = "created_by", nullable = false, updatable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<QuestionJpaEntity> questions = new ArrayList<>();

    /**
     * Association rows only. Category itself is not cascaded — it has its own lifecycle.
     */
    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<QuizCategoryJpaEntity> quizCategories = new HashSet<>();

    protected QuizJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public QuizStatusJpa getStatus() {
        return status;
    }

    public void setStatus(QuizStatusJpa status) {
        this.status = status;
    }

    public QuizDifficultyJpa getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuizDifficultyJpa difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public boolean isRandomQuestions() {
        return randomQuestions;
    }

    public void setRandomQuestions(boolean randomQuestions) {
        this.randomQuestions = randomQuestions;
    }

    public boolean isRandomAnswers() {
        return randomAnswers;
    }

    public void setRandomAnswers(boolean randomAnswers) {
        this.randomAnswers = randomAnswers;
    }

    public boolean isShowCorrectAnswer() {
        return showCorrectAnswer;
    }

    public void setShowCorrectAnswer(boolean showCorrectAnswer) {
        this.showCorrectAnswer = showCorrectAnswer;
    }

    public boolean isShowRanking() {
        return showRanking;
    }

    public void setShowRanking(boolean showRanking) {
        this.showRanking = showRanking;
    }

    public boolean isAllowRetry() {
        return allowRetry;
    }

    public void setAllowRetry(boolean allowRetry) {
        this.allowRetry = allowRetry;
    }

    public boolean isShowTimer() {
        return showTimer;
    }

    public void setShowTimer(boolean showTimer) {
        this.showTimer = showTimer;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
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

    public List<QuestionJpaEntity> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionJpaEntity> questions) {
        this.questions = questions;
    }

    public Set<QuizCategoryJpaEntity> getQuizCategories() {
        return quizCategories;
    }

    public void setQuizCategories(Set<QuizCategoryJpaEntity> quizCategories) {
        this.quizCategories = quizCategories;
    }
}
