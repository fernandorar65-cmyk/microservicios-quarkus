package kahoot.clabs.quiz.infrastructure.persistence.postgres.mapper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.entity.Question;
import kahoot.clabs.quiz.domain.entity.QuizCategory;
import kahoot.clabs.quiz.domain.valueobject.EstimatedTime;
import kahoot.clabs.quiz.domain.valueobject.QuizDifficulty;
import kahoot.clabs.quiz.domain.valueobject.QuizSettings;
import kahoot.clabs.quiz.domain.valueobject.QuizStatus;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.QuizCategoryJpaEntity;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.QuizJpaEntity;

public final class QuizMapper {

    private QuizMapper() {
    }

    public static QuizJpaEntity toEntity(Quiz quiz) {
        QuizJpaEntity entity = QuizJpaEntity.create();
        entity.setId(quiz.getId());
        entity.setOrganizationId(quiz.getOrganizationId());
        entity.setCreatedBy(quiz.getCreatedById());
        entity.setTitle(quiz.getTitle().value());
        entity.setDescription(quiz.getDescription());
        entity.setThumbnailUrl(quiz.getThumbnail());
        entity.setStatus(quiz.getStatus().name());
        entity.setDifficulty(quiz.getDifficulty().name());
        entity.setEstimatedTimeMinutes(quiz.getEstimatedTime() == null
                ? null
                : Math.toIntExact(quiz.getEstimatedTime().toMinutes()));
        entity.setPlayCount(quiz.getPlayCount());
        entity.setAverageRating(BigDecimal.valueOf(quiz.getAverageRating()));
        entity.setTemplate(quiz.isTemplate());
        applySettings(entity, quiz.getSettings());
        entity.setCreatedAt(quiz.getCreatedAt());
        entity.setUpdatedAt(quiz.getUpdatedAt());
        entity.setCategories(quiz.getCategories().stream()
                .map(category -> toEntity(entity, category))
                .collect(Collectors.toCollection(HashSet::new)));
        entity.setQuestions(quiz.getQuestions().stream()
                .map(question -> QuestionMapper.toEntity(question, entity))
                .toList());
        return entity;
    }

    public static Quiz toDomain(QuizJpaEntity entity) {
        List<QuizCategory> categories = entity.getCategories().stream()
                .map(QuizMapper::toDomain)
                .toList();
        List<Question> questions = entity.getQuestions().stream()
                .map(QuestionMapper::toDomain)
                .toList();
        return Quiz.rehydrate(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getCreatedBy(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getThumbnailUrl(),
                QuizStatus.valueOf(entity.getStatus()),
                QuizDifficulty.valueOf(entity.getDifficulty()),
                entity.getEstimatedTimeMinutes() == null
                        ? null
                        : EstimatedTime.ofMinutes(entity.getEstimatedTimeMinutes()),
                QuizSettings.of(
                        entity.isRandomQuestions(),
                        entity.isRandomAnswers(),
                        entity.isShowCorrectAnswer(),
                        entity.isShowRanking(),
                        entity.isAllowRetry(),
                        entity.isShowTimer(),
                        entity.isMusicEnabled()),
                entity.getPlayCount(),
                entity.getAverageRating().doubleValue(),
                entity.isTemplate(),
                categories,
                questions,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private static QuizCategoryJpaEntity toEntity(QuizJpaEntity quiz, QuizCategory category) {
        return QuizCategoryJpaEntity.link(quiz, category.getCategoryId());
    }

    private static QuizCategory toDomain(QuizCategoryJpaEntity entity) {
        return QuizCategory.of(entity.getQuiz().getId(), entity.getCategory().getId());
    }

    private static void applySettings(QuizJpaEntity entity, QuizSettings settings) {
        entity.setRandomQuestions(settings.isRandomQuestions());
        entity.setRandomAnswers(settings.isRandomAnswers());
        entity.setShowCorrectAnswer(settings.isShowCorrectAnswer());
        entity.setShowRanking(settings.isShowRanking());
        entity.setAllowRetry(settings.isAllowRetry());
        entity.setShowTimer(settings.isShowTimer());
        entity.setMusicEnabled(settings.isMusicEnabled());
    }
}
