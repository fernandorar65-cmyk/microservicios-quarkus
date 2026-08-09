package kahoot.clabs.quiz.domain.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.quiz.domain.entity.AnswerOption;
import kahoot.clabs.quiz.domain.entity.Question;
import kahoot.clabs.quiz.domain.entity.QuestionAsset;
import kahoot.clabs.quiz.domain.entity.QuizCategory;
import kahoot.clabs.quiz.domain.event.QuizPublishedEvent;
import kahoot.clabs.quiz.domain.valueobject.EstimatedTime;
import kahoot.clabs.quiz.domain.valueobject.MediaType;
import kahoot.clabs.quiz.domain.valueobject.MediaUrl;
import kahoot.clabs.quiz.domain.valueobject.Points;
import kahoot.clabs.quiz.domain.valueobject.QuestionType;
import kahoot.clabs.quiz.domain.valueobject.QuizDifficulty;
import kahoot.clabs.quiz.domain.valueobject.QuizSettings;
import kahoot.clabs.quiz.domain.valueobject.QuizStatus;
import kahoot.clabs.quiz.domain.valueobject.QuizTitle;
import kahoot.clabs.quiz.domain.valueobject.TimeLimit;
import kahoot.clabs.quiz.domain.shared.AggregateRoot;
import kahoot.clabs.quiz.domain.shared.DomainException;

public class Quiz extends AggregateRoot {

    private final UUID organizationId;
    private final UUID createdById;
    private final List<QuizCategory> categories = new ArrayList<>();

    private QuizTitle title;
    private String description;
    private String thumbnail;

    private QuizStatus status;
    private QuizDifficulty difficulty;
    private EstimatedTime estimatedTime;

    private final List<Question> questions = new ArrayList<>();
    private QuizSettings settings;

    private int playCount;
    private double averageRating;
    private boolean template;

    private Quiz(UUID organizationId, QuizTitle title, UUID createdById) {
        this(null, organizationId, title, createdById, null, null);
    }

    private Quiz(
            UUID id,
            UUID organizationId,
            QuizTitle title,
            UUID createdById,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        if (organizationId == null) {
            throw new DomainException("Organization id is required");
        }
        if (createdById == null) {
            throw new DomainException("CreatedBy id is required");
        }
        this.organizationId = organizationId;
        this.title = title;
        this.createdById = createdById;
        this.status = QuizStatus.DRAFT;
        this.difficulty = QuizDifficulty.EASY;
        this.settings = QuizSettings.defaultSettings();
    }

    public static Quiz create(UUID organizationId, String title, UUID createdById) {
        return new Quiz(organizationId, QuizTitle.of(title), createdById);
    }

    public static Quiz rehydrate(
            UUID id,
            UUID organizationId,
            UUID createdById,
            String title,
            String description,
            String thumbnail,
            QuizStatus status,
            QuizDifficulty difficulty,
            EstimatedTime estimatedTime,
            QuizSettings settings,
            int playCount,
            double averageRating,
            boolean template,
            List<QuizCategory> categories,
            List<Question> questions,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        Quiz quiz = new Quiz(
                id, organizationId, QuizTitle.of(title), createdById, createdAt, updatedAt);
        quiz.description = description;
        quiz.thumbnail = thumbnail;
        quiz.status = status != null ? status : QuizStatus.DRAFT;
        quiz.difficulty = difficulty != null ? difficulty : QuizDifficulty.EASY;
        quiz.estimatedTime = estimatedTime;
        quiz.settings = settings != null ? settings : QuizSettings.defaultSettings();
        quiz.playCount = playCount;
        quiz.averageRating = averageRating;
        quiz.template = template;
        if (categories != null) {
            quiz.categories.addAll(categories);
        }
        if (questions != null) {
            quiz.questions.addAll(questions);
        }
        return quiz;
    }

    public Question addQuestion(String questionTitle, QuestionType type) {
        ensureEditable();
        Question question = Question.create(questionTitle, type);
        question.assignQuizId(getId());
        question.assignOrderIndex(questions.size() + 1);
        questions.add(question);
        touch();
        return question;
    }

    public void removeQuestion(UUID questionId) {
        ensureEditable();
        Question question = requireQuestion(questionId);
        questions.remove(question);
        reindexQuestions();
        touch();
    }

    public void updateQuestion(
            UUID questionId,
            String title,
            String description,
            QuizDifficulty difficulty,
            int points,
            int timeLimitSeconds) {
        ensureEditable();
        Question question = requireQuestion(questionId);
        question.rename(title);
        question.changeDescription(description);
        question.changeDifficulty(difficulty);
        question.changePoints(Points.of(points));
        question.changeTimeLimit(TimeLimit.ofSeconds(timeLimitSeconds));
        touch();
    }

    public void reorderQuestions(List<UUID> orderedQuestionIds) {
        ensureEditable();
        if (orderedQuestionIds == null || orderedQuestionIds.size() != questions.size()) {
            throw new DomainException("The question order must contain every question exactly once");
        }
        List<Question> reordered = new ArrayList<>();
        for (UUID questionId : orderedQuestionIds) {
            Question question = requireQuestion(questionId);
            if (reordered.contains(question)) {
                throw new DomainException("A question cannot be repeated in the order");
            }
            reordered.add(question);
        }
        questions.clear();
        questions.addAll(reordered);
        reindexQuestions();
        touch();
    }

    public void addAnswerOption(UUID questionId, String text, boolean correct) {
        ensureEditable();
        Question question = requireQuestion(questionId);
        question.addAnswerOption(AnswerOption.create(text, correct));
        touch();
    }

    public void updateAnswerOption(UUID questionId, UUID optionId, String text, boolean correct) {
        ensureEditable();
        requireQuestion(questionId).updateAnswerOption(optionId, text, correct);
        touch();
    }

    public void removeAnswerOption(UUID questionId, UUID optionId) {
        ensureEditable();
        requireQuestion(questionId).removeAnswerOption(optionId);
        touch();
    }

    public void reorderAnswerOptions(UUID questionId, List<UUID> orderedOptionIds) {
        ensureEditable();
        requireQuestion(questionId).reorderAnswerOptions(orderedOptionIds);
        touch();
    }

    public void attachAsset(UUID questionId, MediaType type, String url) {
        ensureEditable();
        Question question = requireQuestion(questionId);
        question.attachAsset(QuestionAsset.create(type, MediaUrl.of(url)));
        touch();
    }

    public void attachAsset(
            UUID questionId,
            MediaType type,
            String url,
            String thumbnailUrl,
            String altText,
            Integer durationSeconds) {
        attachAsset(questionId, type, url);
        Question question = requireQuestion(questionId);
        question.updateAsset(
                question.getAsset().getId(),
                type,
                MediaUrl.of(url),
                thumbnailUrl == null || thumbnailUrl.isBlank() ? null : MediaUrl.of(thumbnailUrl),
                altText,
                durationSeconds);
    }

    public void updateQuestionAsset(
            UUID questionId,
            UUID assetId,
            MediaType type,
            String url,
            String thumbnailUrl,
            String altText,
            Integer durationSeconds) {
        ensureEditable();
        requireQuestion(questionId).updateAsset(
                assetId,
                type,
                MediaUrl.of(url),
                thumbnailUrl == null || thumbnailUrl.isBlank() ? null : MediaUrl.of(thumbnailUrl),
                altText,
                durationSeconds);
        touch();
    }

    public void removeQuestionAsset(UUID questionId, UUID assetId) {
        ensureEditable();
        requireQuestion(questionId).removeAsset(assetId);
        touch();
    }

    public void changeQuestionDifficulty(UUID questionId, QuizDifficulty difficulty) {
        ensureEditable();
        requireQuestion(questionId).changeDifficulty(difficulty);
        touch();
    }

    public void changeQuestionPoints(UUID questionId, int points) {
        ensureEditable();
        requireQuestion(questionId).changePoints(Points.of(points));
        touch();
    }

    public void changeQuestionTimeLimit(UUID questionId, int seconds) {
        ensureEditable();
        requireQuestion(questionId).changeTimeLimit(TimeLimit.ofSeconds(seconds));
        touch();
    }

    public void rename(String newTitle) {
        ensureEditable();
        this.title = QuizTitle.of(newTitle);
        touch();
    }

    public void changeDescription(String description) {
        ensureEditable();
        this.description = description;
        touch();
    }

    public void changeThumbnail(String thumbnail) {
        ensureEditable();
        this.thumbnail = thumbnail;
        touch();
    }

    public void assignCategory(UUID categoryId) {
        addCategory(categoryId);
    }

    public void addCategory(UUID categoryId) {
        ensureEditable();
        QuizCategory category = QuizCategory.of(getId(), categoryId);
        if (!categories.contains(category)) {
            categories.add(category);
            touch();
        }
    }

    public void removeCategory(UUID categoryId) {
        ensureEditable();
        if (categories.removeIf(category -> category.getCategoryId().equals(categoryId))) {
            touch();
        }
    }

    public void changeDifficulty(QuizDifficulty difficulty) {
        ensureEditable();
        if (difficulty == null) {
            throw new DomainException("Difficulty is required");
        }
        this.difficulty = difficulty;
        touch();
    }

    public void changeEstimatedTime(EstimatedTime estimatedTime) {
        ensureEditable();
        this.estimatedTime = estimatedTime;
        touch();
    }

    public void changeSettings(QuizSettings settings) {
        ensureEditable();
        if (settings == null) {
            throw new DomainException("Settings are required");
        }
        this.settings = settings;
        touch();
    }

    public void publish() {
        if (status == QuizStatus.ARCHIVED) {
            throw new DomainException("Archived quizzes cannot be published");
        }
        if (questions.isEmpty()) {
            throw new DomainException("Cannot publish a quiz without questions");
        }
        for (Question question : questions) {
            question.assertReadyToPublish();
        }
        this.status = QuizStatus.PUBLISHED;
        touch();
        registerEvent(new QuizPublishedEvent(getId(), organizationId, createdById));
    }

    public void archive() {
        this.status = QuizStatus.ARCHIVED;
        touch();
    }

    public Quiz duplicate(UUID newCreatedById) {
        Quiz copy = Quiz.create(organizationId, title.value() + " (copy)", newCreatedById);
        copy.changeDescription(description);
        copy.changeThumbnail(thumbnail);
        copy.changeDifficulty(difficulty);
        copy.changeEstimatedTime(estimatedTime);
        copy.changeSettings(settings);
        categories.forEach(category -> copy.assignCategory(category.getCategoryId()));

        for (Question originalQuestion : questions) {
            Question copiedQuestion = copy.addQuestion(originalQuestion.getTitle(), originalQuestion.getType());
            copy.updateQuestion(
                    copiedQuestion.getId(),
                    originalQuestion.getTitle(),
                    originalQuestion.getDescription(),
                    originalQuestion.getDifficulty(),
                    originalQuestion.getPoints().value(),
                    originalQuestion.getTimeLimit().seconds());
            originalQuestion.getOptions().forEach(option ->
                    copy.addAnswerOption(copiedQuestion.getId(), option.getText(), option.isCorrect()));
            if (originalQuestion.getAsset() != null) {
                QuestionAsset asset = originalQuestion.getAsset();
                copy.attachAsset(
                        copiedQuestion.getId(),
                        asset.getType(),
                        asset.getUrl().value(),
                        asset.getThumbnailUrl() == null ? null : asset.getThumbnailUrl().value(),
                        asset.getAltText(),
                        asset.getDurationSeconds());
            }
        }
        return copy;
    }

    public void incrementPlayCount() {
        if (status != QuizStatus.PUBLISHED) {
            throw new DomainException("Only published quizzes can be played");
        }
        this.playCount++;
        touch();
    }

    private Question requireQuestion(UUID questionId) {
        return questions.stream()
                .filter(question -> question.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new DomainException("Question not found: " + questionId));
    }

    private void reindexQuestions() {
        for (int index = 0; index < questions.size(); index++) {
            questions.get(index).assignOrderIndex(index + 1);
        }
    }

    private void ensureEditable() {
        if (status == QuizStatus.ARCHIVED) {
            throw new DomainException("Archived quizzes cannot be modified");
        }
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getCategoryId() {
        return categories.isEmpty() ? null : categories.getFirst().getCategoryId();
    }

    public List<QuizCategory> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public QuizTitle getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public QuizStatus getStatus() {
        return status;
    }

    public QuizDifficulty getDifficulty() {
        return difficulty;
    }

    public EstimatedTime getEstimatedTime() {
        return estimatedTime;
    }

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public QuizSettings getSettings() {
        return settings;
    }

    public int getPlayCount() {
        return playCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public boolean isTemplate() {
        return template;
    }
}
