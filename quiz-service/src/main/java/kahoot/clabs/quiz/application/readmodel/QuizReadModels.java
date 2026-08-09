package kahoot.clabs.quiz.application.readmodel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.entity.AnswerOption;
import kahoot.clabs.quiz.domain.entity.Question;
import kahoot.clabs.quiz.domain.entity.QuestionAsset;
import kahoot.clabs.quiz.domain.valueobject.QuizSettings;

public final class QuizReadModels {

    private QuizReadModels() {
    }

    public static QuizReadModel from(Quiz quiz) {
        QuizSettings settings = quiz.getSettings();
        QuizReadModel readModel = new QuizReadModel();
        readModel.setId(quiz.getId());
        readModel.setOrganizationId(quiz.getOrganizationId());
        readModel.setCreatedBy(quiz.getCreatedById());
        readModel.setTitle(quiz.getTitle().value());
        readModel.setDescription(quiz.getDescription());
        readModel.setThumbnailUrl(quiz.getThumbnail());
        readModel.setStatus(quiz.getStatus().name());
        readModel.setDifficulty(quiz.getDifficulty().name());
        readModel.setEstimatedTimeMinutes(
                quiz.getEstimatedTime() == null ? null : Math.toIntExact(quiz.getEstimatedTime().toMinutes()));
        readModel.setPlayCount(quiz.getPlayCount());
        readModel.setAverageRating(BigDecimal.valueOf(quiz.getAverageRating()));
        readModel.setTemplate(quiz.isTemplate());
        readModel.setSettings(toSettings(settings));
        readModel.setCategories(quiz.getCategories().stream()
                .map(category -> {
                    QuizCategoryReadModel categoryReadModel = new QuizCategoryReadModel();
                    categoryReadModel.setId(category.getCategoryId());
                    return categoryReadModel;
                })
                .toList());
        readModel.setQuestionCount(quiz.getQuestions().size());
        readModel.setQuestions(quiz.getQuestions().stream().map(QuizReadModels::toQuestion).toList());
        readModel.setCreatedAt(toInstant(quiz.getCreatedAt()));
        readModel.setUpdatedAt(toInstant(quiz.getUpdatedAt()));
        return readModel;
    }

    private static QuizSettingsReadModel toSettings(QuizSettings settings) {
        QuizSettingsReadModel readModel = new QuizSettingsReadModel();
        readModel.setRandomQuestions(settings.isRandomQuestions());
        readModel.setRandomAnswers(settings.isRandomAnswers());
        readModel.setShowCorrectAnswer(settings.isShowCorrectAnswer());
        readModel.setShowRanking(settings.isShowRanking());
        readModel.setAllowRetry(settings.isAllowRetry());
        readModel.setShowTimer(settings.isShowTimer());
        readModel.setMusicEnabled(settings.isMusicEnabled());
        return readModel;
    }

    private static QuizQuestionReadModel toQuestion(Question question) {
        QuizQuestionReadModel readModel = new QuizQuestionReadModel();
        readModel.setId(question.getId());
        readModel.setTitle(question.getTitle());
        readModel.setDescription(question.getDescription());
        readModel.setType(question.getType().name());
        readModel.setDifficulty(question.getDifficulty().name());
        readModel.setExplanation(question.getExplanation());
        readModel.setOrderIndex(question.getOrderIndex());
        readModel.setTimeLimitSeconds(question.getTimeLimit().seconds());
        readModel.setPoints(question.getPoints().value());
        readModel.setAnswerOptions(question.getOptions().stream().map(QuizReadModels::toOption).toList());
        readModel.setAsset(toAsset(question.getAsset()));
        return readModel;
    }

    private static QuizAnswerOptionReadModel toOption(AnswerOption option) {
        QuizAnswerOptionReadModel readModel = new QuizAnswerOptionReadModel();
        readModel.setId(option.getId());
        readModel.setText(option.getText());
        readModel.setCorrect(option.isCorrect());
        readModel.setExplanation(option.getExplanation());
        readModel.setOrderIndex(option.getOrderIndex());
        return readModel;
    }

    private static QuizAssetReadModel toAsset(QuestionAsset asset) {
        if (asset == null) {
            return null;
        }
        QuizAssetReadModel readModel = new QuizAssetReadModel();
        readModel.setId(asset.getId());
        readModel.setType(asset.getType().name());
        readModel.setUrl(asset.getUrl().value());
        readModel.setThumbnailUrl(asset.getThumbnailUrl() == null ? null : asset.getThumbnailUrl().value());
        readModel.setAltText(asset.getAltText());
        readModel.setDurationSeconds(asset.getDurationSeconds());
        return readModel;
    }

    private static Instant toInstant(java.time.LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }

    public static List<QuizCategoryReadModel> enrichCategories(
            List<QuizCategoryReadModel> categories, List<CategoryReadModel> knownCategories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        List<QuizCategoryReadModel> enriched = new ArrayList<>();
        for (QuizCategoryReadModel category : categories) {
            QuizCategoryReadModel copy = new QuizCategoryReadModel();
            copy.setId(category.getId());
            knownCategories.stream()
                    .filter(known -> known.getId().equals(category.getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            known -> {
                                copy.setName(known.getName());
                                copy.setColor(known.getColor());
                                copy.setIcon(known.getIcon());
                            },
                            () -> copy.setName(category.getName()));
            enriched.add(copy);
        }
        return enriched;
    }
}
