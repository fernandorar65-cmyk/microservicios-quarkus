package kahoot.clabs.quiz.infrastructure.persistence.postgres.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_assets")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionAssetJpaEntity {

    public static QuestionAssetJpaEntity create() {
        return new QuestionAssetJpaEntity();
    }

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private QuestionJpaEntity question;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
