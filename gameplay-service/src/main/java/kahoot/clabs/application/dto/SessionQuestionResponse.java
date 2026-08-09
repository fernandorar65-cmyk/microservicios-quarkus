package kahoot.clabs.application.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.application.readmodel.GameSessionReadModel;

public record SessionQuestionResponse(
        UUID id,
        int orderIndex,
        int points,
        int timeLimitSeconds,
        String title,
        String description,
        String questionType,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        List<OptionResponse> options) {

    public static SessionQuestionResponse from(GameSessionReadModel.SessionQuestion question, boolean revealCorrect) {
        return new SessionQuestionResponse(
                question.getId(),
                question.getOrderIndex(),
                question.getPoints(),
                question.getTimeLimitSeconds(),
                question.getTitle(),
                question.getDescription(),
                question.getQuestionType(),
                toLocalDateTime(question.getOpenedAt()),
                toLocalDateTime(question.getClosedAt()),
                question.getAnswerOptions().stream()
                        .map(option -> OptionResponse.from(option, revealCorrect))
                        .toList());
    }

    public record OptionResponse(UUID id, String text, int orderIndex, Boolean correct) {

        private static OptionResponse from(
                GameSessionReadModel.SessionAnswerOption option, boolean revealCorrect) {
            return new OptionResponse(
                    option.getId(),
                    option.getText(),
                    option.getOrderIndex(),
                    revealCorrect ? option.isCorrect() : null);
        }
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
