package kahoot.clabs.quiz.infrastructure.persistence.mongo.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.quiz.application.port.out.read.QuizReadPort;
import kahoot.clabs.quiz.application.readmodel.QuizAnswerOptionReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizAssetReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizCategoryReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizCreatorReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizQuestionReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizSettingsReadModel;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizAnswerOptionEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizAssetEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizCategoryEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizCreatorEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizQuestionEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizReadDocument;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizSettingsEmbed;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.repository.QuizMongoRepository;

@ApplicationScoped
public class QuizMongoReadAdapter implements QuizReadPort {

    private final QuizMongoRepository quizMongoRepository;

    public QuizMongoReadAdapter(QuizMongoRepository quizMongoRepository) {
        this.quizMongoRepository = quizMongoRepository;
    }

    @Override
    public Optional<QuizReadModel> findById(UUID id) {
        return quizMongoRepository.findByIdOptional(id).map(this::toReadModel);
    }

    @Override
    public List<QuizReadModel> findByOrganization(UUID organizationId) {
        return quizMongoRepository.list("organizationId", organizationId).stream()
                .map(this::toReadModel)
                .toList();
    }

    private QuizReadModel toReadModel(QuizReadDocument document) {
        QuizReadModel readModel = new QuizReadModel();
        readModel.setId(document.getId());
        readModel.setOrganizationId(document.getOrganizationId());
        readModel.setCreatedBy(document.getCreatedBy());
        readModel.setCreator(toCreatorReadModel(document.getCreator()));
        readModel.setTitle(document.getTitle());
        readModel.setDescription(document.getDescription());
        readModel.setThumbnailUrl(document.getThumbnailUrl());
        readModel.setStatus(document.getStatus());
        readModel.setDifficulty(document.getDifficulty());
        readModel.setEstimatedTimeMinutes(document.getEstimatedTimeMinutes());
        readModel.setPlayCount(document.getPlayCount());
        readModel.setAverageRating(document.getAverageRating());
        readModel.setTemplate(document.isTemplate());
        readModel.setSettings(toSettingsReadModel(document.getSettings()));
        readModel.setCategories(toCategoryReadModels(document.getCategories()));
        readModel.setQuestions(toQuestionReadModels(document.getQuestions()));
        readModel.setQuestionCount(document.getQuestionCount());
        readModel.setCreatedAt(document.getCreatedAt());
        readModel.setUpdatedAt(document.getUpdatedAt());
        return readModel;
    }

    private QuizCreatorReadModel toCreatorReadModel(QuizCreatorEmbed embed) {
        if (embed == null) {
            return null;
        }

        QuizCreatorReadModel readModel = new QuizCreatorReadModel();
        readModel.setId(embed.getId());
        readModel.setName(embed.getName());
        return readModel;
    }

    private QuizSettingsReadModel toSettingsReadModel(QuizSettingsEmbed embed) {
        if (embed == null) {
            return null;
        }

        QuizSettingsReadModel readModel = new QuizSettingsReadModel();
        readModel.setRandomQuestions(embed.isRandomQuestions());
        readModel.setRandomAnswers(embed.isRandomAnswers());
        readModel.setShowCorrectAnswer(embed.isShowCorrectAnswer());
        readModel.setShowRanking(embed.isShowRanking());
        readModel.setAllowRetry(embed.isAllowRetry());
        readModel.setShowTimer(embed.isShowTimer());
        readModel.setMusicEnabled(embed.isMusicEnabled());
        return readModel;
    }

    private List<QuizCategoryReadModel> toCategoryReadModels(List<QuizCategoryEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toCategoryReadModel).toList();
    }

    private QuizCategoryReadModel toCategoryReadModel(QuizCategoryEmbed embed) {
        QuizCategoryReadModel readModel = new QuizCategoryReadModel();
        readModel.setId(embed.getId());
        readModel.setName(embed.getName());
        readModel.setColor(embed.getColor());
        readModel.setIcon(embed.getIcon());
        return readModel;
    }

    private List<QuizQuestionReadModel> toQuestionReadModels(List<QuizQuestionEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toQuestionReadModel).toList();
    }

    private QuizQuestionReadModel toQuestionReadModel(QuizQuestionEmbed embed) {
        QuizQuestionReadModel readModel = new QuizQuestionReadModel();
        readModel.setId(embed.getId());
        readModel.setTitle(embed.getTitle());
        readModel.setDescription(embed.getDescription());
        readModel.setType(embed.getType());
        readModel.setDifficulty(embed.getDifficulty());
        readModel.setExplanation(embed.getExplanation());
        readModel.setOrderIndex(embed.getOrderIndex());
        readModel.setTimeLimitSeconds(embed.getTimeLimitSeconds());
        readModel.setPoints(embed.getPoints());
        readModel.setAsset(toAssetReadModel(embed.getAsset()));
        readModel.setAnswerOptions(toAnswerOptionReadModels(embed.getAnswerOptions()));
        return readModel;
    }

    private QuizAssetReadModel toAssetReadModel(QuizAssetEmbed embed) {
        if (embed == null) {
            return null;
        }

        QuizAssetReadModel readModel = new QuizAssetReadModel();
        readModel.setId(embed.getId());
        readModel.setType(embed.getType());
        readModel.setUrl(embed.getUrl());
        readModel.setThumbnailUrl(embed.getThumbnailUrl());
        readModel.setAltText(embed.getAltText());
        readModel.setDurationSeconds(embed.getDurationSeconds());
        return readModel;
    }

    private List<QuizAnswerOptionReadModel> toAnswerOptionReadModels(List<QuizAnswerOptionEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toAnswerOptionReadModel).toList();
    }

    private QuizAnswerOptionReadModel toAnswerOptionReadModel(QuizAnswerOptionEmbed embed) {
        QuizAnswerOptionReadModel readModel = new QuizAnswerOptionReadModel();
        readModel.setId(embed.getId());
        readModel.setText(embed.getText());
        readModel.setCorrect(embed.isCorrect());
        readModel.setExplanation(embed.getExplanation());
        readModel.setOrderIndex(embed.getOrderIndex());
        return readModel;
    }
}
