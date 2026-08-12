package kahoot.clabs.quiz.domain.entity;

import java.util.UUID;

import kahoot.clabs.quiz.domain.valueobject.MediaType;
import kahoot.clabs.quiz.domain.valueobject.MediaUrl;
import kahoot.clabs.quiz.domain.shared.BaseEntity;
import kahoot.clabs.quiz.domain.shared.DomainException;

public class QuestionAsset extends BaseEntity {

    private UUID questionId;
    private MediaType type;
    private MediaUrl url;
    private MediaUrl thumbnailUrl;
    private String altText;
    private Integer durationSeconds;

    private QuestionAsset(MediaType type, MediaUrl url) {
        this(null, type, url);
    }

    private QuestionAsset(UUID id, MediaType type, MediaUrl url) {
        super(id);
        if (type == null) {
            throw new DomainException("Media type is required");
        }
        if (url == null) {
            throw new DomainException("Media URL is required");
        }
        this.type = type;
        this.url = url;
    }

    public static QuestionAsset create(MediaType type, MediaUrl url) {
        return new QuestionAsset(type, url);
    }

    public static QuestionAsset rehydrate(
            UUID id,
            UUID questionId,
            MediaType type,
            MediaUrl url,
            MediaUrl thumbnailUrl,
            String altText,
            Integer durationSeconds) {
        QuestionAsset asset = new QuestionAsset(id, type, url);
        asset.questionId = questionId;
        asset.thumbnailUrl = thumbnailUrl;
        asset.altText = altText;
        asset.changeDurationSeconds(durationSeconds);
        return asset;
    }

    void assignQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    void update(MediaType type, MediaUrl url, MediaUrl thumbnailUrl, String altText, Integer durationSeconds) {
        if (type == null || url == null) {
            throw new DomainException("Media type and URL are required");
        }
        this.type = type;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.altText = altText;
        changeDurationSeconds(durationSeconds);
    }

    public void changeThumbnail(MediaUrl thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void changeAltText(String altText) {
        this.altText = altText;
    }

    public void changeDurationSeconds(Integer durationSeconds) {
        if (durationSeconds != null && durationSeconds <= 0) {
            throw new DomainException("Media duration must be positive");
        }
        this.durationSeconds = durationSeconds;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public MediaType getType() {
        return type;
    }

    public MediaUrl getUrl() {
        return url;
    }

    public MediaUrl getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getAltText() {
        return altText;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }
}
