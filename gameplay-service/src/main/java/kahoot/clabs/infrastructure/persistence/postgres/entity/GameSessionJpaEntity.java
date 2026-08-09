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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import kahoot.clabs.gameplay.infrastructure.persistence.postgres.enums.SessionStatusJpa;

@Entity
@Table(
        name = "game_sessions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_game_sessions_join_code",
                columnNames = "join_code"))
public class GameSessionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** External reference to organization-service. No FK. */
    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    /** External reference to quiz-service. No FK. */
    @Column(name = "quiz_id", nullable = false, updatable = false)
    private UUID quizId;

    /** External reference to identity-service. No FK. */
    @Column(name = "host_user_id", nullable = false, updatable = false)
    private UUID hostUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatusJpa status;

    @Column(name = "join_code", nullable = false, length = 12)
    private String joinCode;

    @Column(name = "current_question_index", nullable = false)
    private int currentQuestionIndex;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "session",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<SessionPlayerJpaEntity> players = new ArrayList<>();

    @OneToMany(
            mappedBy = "session",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<SessionQuestionJpaEntity> questions = new ArrayList<>();

    protected GameSessionJpaEntity() {
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

    public UUID getQuizId() {
        return quizId;
    }

    public void setQuizId(UUID quizId) {
        this.quizId = quizId;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(UUID hostUserId) {
        this.hostUserId = hostUserId;
    }

    public SessionStatusJpa getStatus() {
        return status;
    }

    public void setStatus(SessionStatusJpa status) {
        this.status = status;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
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

    public List<SessionPlayerJpaEntity> getPlayers() {
        return players;
    }

    public void setPlayers(List<SessionPlayerJpaEntity> players) {
        this.players = players;
    }

    public List<SessionQuestionJpaEntity> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SessionQuestionJpaEntity> questions) {
        this.questions = questions;
    }
}
