package kahoot.clabs.quiz.infrastructure.persistence.mongo.adapter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.port.out.QuizProjectionPort;
import kahoot.clabs.quiz.application.port.out.read.CategoryReadPort;
import kahoot.clabs.quiz.application.readmodel.CategoryReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizCategoryReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizReadModels;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizAnswerOptionEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizAssetEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizCategoryEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizQuestionEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizReadDocument;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizSettingsEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.repository.QuizMongoRepository;

@ApplicationScoped
public class QuizMongoProjectionAdapter implements QuizProjectionPort {

    private final QuizMongoRepository quizMongoRepository;
    private final CategoryReadPort categoryReadPort;

    @Inject
    public QuizMongoProjectionAdapter(QuizMongoRepository quizMongoRepository, CategoryReadPort categoryReadPort) {
        this.quizMongoRepository = quizMongoRepository;
        this.categoryReadPort = categoryReadPort;
    }

    @Override
    public void save(QuizReadModel readModel) {
        List<CategoryReadModel> knownCategories = loadCategories(readModel.getCategories());
        readModel.setCategories(QuizReadModels.enrichCategories(readModel.getCategories(), knownCategories));
        quizMongoRepository.persistOrUpdate(toDocument(readModel));
    }

    @Override
    public void deleteById(UUID id) {
        quizMongoRepository.deleteById(id);
    }

    private List<CategoryReadModel> loadCategories(List<QuizCategoryReadModel> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }
        return categories.stream()
                .map(QuizCategoryReadModel::getId)
                .map(categoryReadPort::findById)
                .flatMap(java.util.Optional::stream)
                .toList();
    }

    private QuizReadDocument toDocument(QuizReadModel readModel) {
        QuizReadDocument document = new QuizReadDocument();
        document.setId(readModel.getId());
        document.setOrganizationId(readModel.getOrganizationId());
        document.setCreatedBy(readModel.getCreatedBy());
        document.setTitle(readModel.getTitle());
        document.setDescription(readModel.getDescription());
        document.setThumbnailUrl(readModel.getThumbnailUrl());
        document.setStatus(readModel.getStatus());
        document.setDifficulty(readModel.getDifficulty());
        document.setEstimatedTimeMinutes(readModel.getEstimatedTimeMinutes());
        document.setPlayCount(readModel.getPlayCount());
        document.setAverageRating(readModel.getAverageRating());
        document.setTemplate(readModel.isTemplate());
        document.setSettings(toSettingsEmbed(readModel.getSettings()));
        document.setCategories(toCategoryEmbeds(readModel.getCategories()));
        document.setQuestions(toQuestionEmbeds(readModel.getQuestions()));
        document.setQuestionCount(readModel.getQuestionCount());
        document.setCreatedAt(readModel.getCreatedAt());
        document.setUpdatedAt(readModel.getUpdatedAt());
        return document;
    }

    private QuizSettingsEmbed toSettingsEmbed(kahoot.clabs.quiz.application.readmodel.QuizSettingsReadModel settings) {
        if (settings == null) {
            return null;
        }
        QuizSettingsEmbed embed = new QuizSettingsEmbed();
        embed.setRandomQuestions(settings.isRandomQuestions());
        embed.setRandomAnswers(settings.isRandomAnswers());
        embed.setShowCorrectAnswer(settings.isShowCorrectAnswer());
        embed.setShowRanking(settings.isShowRanking());
        embed.setAllowRetry(settings.isAllowRetry());
        embed.setShowTimer(settings.isShowTimer());
        embed.setMusicEnabled(settings.isMusicEnabled());
        return embed;
    }

    private List<QuizCategoryEmbed> toCategoryEmbeds(List<QuizCategoryReadModel> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }
        return categories.stream().map(this::toCategoryEmbed).toList();
    }

    private QuizCategoryEmbed toCategoryEmbed(QuizCategoryReadModel category) {
        QuizCategoryEmbed embed = new QuizCategoryEmbed();
        embed.setId(category.getId());
        embed.setName(category.getName());
        embed.setColor(category.getColor());
        embed.setIcon(category.getIcon());
        return embed;
    }

    private List<QuizQuestionEmbed> toQuestionEmbeds(
            List<kahoot.clabs.quiz.application.readmodel.QuizQuestionReadModel> questions) {
        if (questions == null || questions.isEmpty()) {
            return Collections.emptyList();
        }
        return questions.stream().map(this::toQuestionEmbed).toList();
    }

    private QuizQuestionEmbed toQuestionEmbed(
            kahoot.clabs.quiz.application.readmodel.QuizQuestionReadModel question) {
        QuizQuestionEmbed embed = new QuizQuestionEmbed();
        embed.setId(question.getId());
        embed.setTitle(question.getTitle());
        embed.setDescription(question.getDescription());
        embed.setType(question.getType());
        embed.setDifficulty(question.getDifficulty());
        embed.setExplanation(question.getExplanation());
        embed.setOrderIndex(question.getOrderIndex());
        embed.setTimeLimitSeconds(question.getTimeLimitSeconds());
        embed.setPoints(question.getPoints());
        embed.setAsset(toAssetEmbed(question.getAsset()));
        embed.setAnswerOptions(toOptionEmbeds(question.getAnswerOptions()));
        return embed;
    }

    private QuizAssetEmbed toAssetEmbed(kahoot.clabs.quiz.application.readmodel.QuizAssetReadModel asset) {
        if (asset == null) {
            return null;
        }
        QuizAssetEmbed embed = new QuizAssetEmbed();
        embed.setId(asset.getId());
        embed.setType(asset.getType());
        embed.setUrl(asset.getUrl());
        embed.setThumbnailUrl(asset.getThumbnailUrl());
        embed.setAltText(asset.getAltText());
        embed.setDurationSeconds(asset.getDurationSeconds());
        return embed;
    }

    private List<QuizAnswerOptionEmbed> toOptionEmbeds(
            List<kahoot.clabs.quiz.application.readmodel.QuizAnswerOptionReadModel> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }
        return options.stream().map(this::toOptionEmbed).toList();
    }

    private QuizAnswerOptionEmbed toOptionEmbed(
            kahoot.clabs.quiz.application.readmodel.QuizAnswerOptionReadModel option) {
        QuizAnswerOptionEmbed embed = new QuizAnswerOptionEmbed();
        embed.setId(option.getId());
        embed.setText(option.getText());
        embed.setCorrect(option.isCorrect());
        embed.setExplanation(option.getExplanation());
        embed.setOrderIndex(option.getOrderIndex());
        return embed;
    }
}
