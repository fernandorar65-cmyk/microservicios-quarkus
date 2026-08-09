package kahoot.clabs.quiz.infrastructure.persistence.postgres.mapper;

import java.time.LocalDateTime;
import java.util.List;

import kahoot.clabs.quiz.domain.entity.AnswerOption;
import kahoot.clabs.quiz.domain.entity.Question;
import kahoot.clabs.quiz.domain.entity.QuestionAsset;
import kahoot.clabs.quiz.domain.valueobject.MediaType;
import kahoot.clabs.quiz.domain.valueobject.MediaUrl;
import kahoot.clabs.quiz.domain.valueobject.Points;
import kahoot.clabs.quiz.domain.valueobject.QuestionType;
import kahoot.clabs.quiz.domain.valueobject.QuizDifficulty;
import kahoot.clabs.quiz.domain.valueobject.TimeLimit;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.AnswerOptionJpaEntity;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.QuestionAssetJpaEntity;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.QuestionJpaEntity;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.QuizJpaEntity;

public final class QuestionMapper {

    private QuestionMapper() {
    }

    public static QuestionJpaEntity toEntity(Question question, QuizJpaEntity quiz) {
        QuestionJpaEntity entity = QuestionJpaEntity.create();
        entity.setId(question.getId());
        entity.setQuiz(quiz);
        entity.setTitle(question.getTitle());
        entity.setDescription(question.getDescription());
        entity.setType(question.getType().name());
        entity.setDifficulty(question.getDifficulty().name());
        entity.setExplanation(question.getExplanation());
        entity.setOrderIndex(question.getOrderIndex());
        entity.setTimeLimitSeconds(question.getTimeLimit().seconds());
        entity.setPoints(question.getPoints().value());
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setAnswerOptions(question.getOptions().stream()
                .map(option -> toEntity(option, entity))
                .toList());
        if (question.getAsset() != null) {
            entity.setAsset(toEntity(question.getAsset(), entity));
        }
        return entity;
    }

    public static Question toDomain(QuestionJpaEntity entity) {
        List<AnswerOption> options = entity.getAnswerOptions().stream()
                .map(QuestionMapper::toDomain)
                .toList();
        QuestionAsset asset = entity.getAsset() == null ? null : toDomain(entity.getAsset());
        return Question.rehydrate(
                entity.getId(),
                entity.getQuiz().getId(),
                entity.getTitle(),
                entity.getDescription(),
                QuestionType.valueOf(entity.getType()),
                Points.of(entity.getPoints()),
                TimeLimit.ofSeconds(entity.getTimeLimitSeconds()),
                entity.getOrderIndex(),
                entity.getExplanation(),
                QuizDifficulty.valueOf(entity.getDifficulty()),
                options,
                asset);
    }

    private static AnswerOptionJpaEntity toEntity(AnswerOption option, QuestionJpaEntity question) {
        AnswerOptionJpaEntity entity = AnswerOptionJpaEntity.create();
        entity.setId(option.getId());
        entity.setQuestion(question);
        entity.setText(option.getText());
        entity.setCorrect(option.isCorrect());
        entity.setExplanation(option.getExplanation());
        entity.setOrderIndex(option.getOrderIndex());
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }

    private static AnswerOption toDomain(AnswerOptionJpaEntity entity) {
        return AnswerOption.rehydrate(
                entity.getId(),
                entity.getQuestion().getId(),
                entity.getText(),
                entity.isCorrect(),
                entity.getOrderIndex(),
                entity.getExplanation());
    }

    private static QuestionAssetJpaEntity toEntity(QuestionAsset asset, QuestionJpaEntity question) {
        QuestionAssetJpaEntity entity = QuestionAssetJpaEntity.create();
        entity.setId(asset.getId());
        entity.setQuestion(question);
        entity.setType(asset.getType().name());
        entity.setUrl(asset.getUrl().value());
        entity.setThumbnailUrl(asset.getThumbnailUrl() == null ? null : asset.getThumbnailUrl().value());
        entity.setAltText(asset.getAltText());
        entity.setDurationSeconds(asset.getDurationSeconds());
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }

    private static QuestionAsset toDomain(QuestionAssetJpaEntity entity) {
        return QuestionAsset.rehydrate(
                entity.getId(),
                entity.getQuestion().getId(),
                MediaType.valueOf(entity.getType()),
                MediaUrl.of(entity.getUrl()),
                entity.getThumbnailUrl() == null ? null : MediaUrl.of(entity.getThumbnailUrl()),
                entity.getAltText(),
                entity.getDurationSeconds());
    }
}
