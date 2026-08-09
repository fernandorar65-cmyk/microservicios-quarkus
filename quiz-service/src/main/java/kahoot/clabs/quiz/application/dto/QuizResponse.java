package kahoot.clabs.quiz.application.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import kahoot.clabs.quiz.application.readmodel.QuizAnswerOptionReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizAssetReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizCategoryReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizQuestionReadModel;
import kahoot.clabs.quiz.application.readmodel.QuizReadModel;
import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.entity.AnswerOption;
import kahoot.clabs.quiz.domain.entity.Question;
import kahoot.clabs.quiz.domain.entity.QuestionAsset;

public record QuizResponse(
        UUID id,
        UUID organizationId,
        UUID createdById,
        String title,
        String description,
        String thumbnail,
        String status,
        String difficulty,
        Long estimatedTimeMinutes,
        int playCount,
        double averageRating,
        boolean template,
        List<UUID> categoryIds,
        int questionCount,
        List<QuestionResponse> questions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static QuizResponse from(Quiz quiz) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getOrganizationId(),
                quiz.getCreatedById(),
                quiz.getTitle().value(),
                quiz.getDescription(),
                quiz.getThumbnail(),
                quiz.getStatus().name(),
                quiz.getDifficulty().name(),
                quiz.getEstimatedTime() == null ? null : quiz.getEstimatedTime().toMinutes(),
                quiz.getPlayCount(),
                quiz.getAverageRating(),
                quiz.isTemplate(),
                quiz.getCategories().stream().map(category -> category.getCategoryId()).toList(),
                quiz.getQuestions().size(),
                quiz.getQuestions().stream().map(QuestionResponse::from).toList(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt());
    }

    public static QuizResponse from(QuizReadModel readModel) {
        return new QuizResponse(
                readModel.getId(),
                readModel.getOrganizationId(),
                readModel.getCreatedBy(),
                readModel.getTitle(),
                readModel.getDescription(),
                readModel.getThumbnailUrl(),
                readModel.getStatus(),
                readModel.getDifficulty(),
                readModel.getEstimatedTimeMinutes() == null ? null : readModel.getEstimatedTimeMinutes().longValue(),
                readModel.getPlayCount(),
                readModel.getAverageRating() == null ? 0.0 : readModel.getAverageRating().doubleValue(),
                readModel.isTemplate(),
                categoryIds(readModel.getCategories()),
                readModel.getQuestionCount(),
                List.of(),
                toLocalDateTime(readModel.getCreatedAt()),
                toLocalDateTime(readModel.getUpdatedAt()));
    }

    public static QuizResponse fromDetails(QuizReadModel readModel) {
        return new QuizResponse(
                readModel.getId(),
                readModel.getOrganizationId(),
                readModel.getCreatedBy(),
                readModel.getTitle(),
                readModel.getDescription(),
                readModel.getThumbnailUrl(),
                readModel.getStatus(),
                readModel.getDifficulty(),
                readModel.getEstimatedTimeMinutes() == null ? null : readModel.getEstimatedTimeMinutes().longValue(),
                readModel.getPlayCount(),
                readModel.getAverageRating() == null ? 0.0 : readModel.getAverageRating().doubleValue(),
                readModel.isTemplate(),
                categoryIds(readModel.getCategories()),
                readModel.getQuestionCount(),
                readModel.getQuestions() == null
                        ? List.of()
                        : readModel.getQuestions().stream().map(QuestionResponse::from).toList(),
                toLocalDateTime(readModel.getCreatedAt()),
                toLocalDateTime(readModel.getUpdatedAt()));
    }

    private static List<UUID> categoryIds(List<QuizCategoryReadModel> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        return categories.stream().map(QuizCategoryReadModel::getId).collect(Collectors.toList());
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public record QuestionResponse(
            UUID id,
            String title,
            String description,
            String type,
            String difficulty,
            int points,
            int timeLimitSeconds,
            int orderIndex,
            List<AnswerOptionResponse> options,
            QuestionAssetResponse asset) {

        private static QuestionResponse from(Question question) {
            return new QuestionResponse(
                    question.getId(),
                    question.getTitle(),
                    question.getDescription(),
                    question.getType().name(),
                    question.getDifficulty().name(),
                    question.getPoints().value(),
                    question.getTimeLimit().seconds(),
                    question.getOrderIndex(),
                    question.getOptions().stream().map(AnswerOptionResponse::from).toList(),
                    QuestionAssetResponse.from(question.getAsset()));
        }

        private static QuestionResponse from(QuizQuestionReadModel question) {
            return new QuestionResponse(
                    question.getId(),
                    question.getTitle(),
                    question.getDescription(),
                    question.getType(),
                    question.getDifficulty(),
                    question.getPoints(),
                    question.getTimeLimitSeconds(),
                    question.getOrderIndex(),
                    question.getAnswerOptions() == null
                            ? Collections.emptyList()
                            : question.getAnswerOptions().stream().map(AnswerOptionResponse::from).toList(),
                    QuestionAssetResponse.from(question.getAsset()));
        }
    }

    public record AnswerOptionResponse(UUID id, String text, int orderIndex) {

        private static AnswerOptionResponse from(AnswerOption option) {
            return new AnswerOptionResponse(option.getId(), option.getText(), option.getOrderIndex());
        }

        private static AnswerOptionResponse from(QuizAnswerOptionReadModel option) {
            return new AnswerOptionResponse(option.getId(), option.getText(), option.getOrderIndex());
        }
    }

    public record QuestionAssetResponse(
            UUID id,
            String type,
            String url,
            String thumbnailUrl,
            String altText,
            Integer durationSeconds) {

        private static QuestionAssetResponse from(QuestionAsset asset) {
            if (asset == null) {
                return null;
            }
            return new QuestionAssetResponse(
                    asset.getId(),
                    asset.getType().name(),
                    asset.getUrl().value(),
                    asset.getThumbnailUrl() == null ? null : asset.getThumbnailUrl().value(),
                    asset.getAltText(),
                    asset.getDurationSeconds());
        }

        private static QuestionAssetResponse from(QuizAssetReadModel asset) {
            if (asset == null) {
                return null;
            }
            return new QuestionAssetResponse(
                    asset.getId(),
                    asset.getType(),
                    asset.getUrl(),
                    asset.getThumbnailUrl(),
                    asset.getAltText(),
                    asset.getDurationSeconds());
        }
    }
}
