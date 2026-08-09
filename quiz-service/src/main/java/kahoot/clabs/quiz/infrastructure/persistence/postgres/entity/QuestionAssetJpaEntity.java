package kahoot.clabs.quiz.infrastructure.persistence.postgres.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import kahoot.clabs.quiz.infrastructure.persistence.postgres.enums.MediaTypeJpa;

/**
 * JPA persistence model for question asset metadata (URL only, no binary).
 */
@Entity
@Table(name = "question_assets")
public class QuestionAssetJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionJpaEntity question;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 30)
    private MediaTypeJpa mediaType;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected QuestionAssetJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public QuestionJpaEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionJpaEntity question) {
        this.question = question;
    }

    public MediaTypeJpa getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaTypeJpa mediaType) {
        this.mediaType = mediaType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
