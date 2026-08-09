package kahoot.clabs.quiz.application.command;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kahoot.clabs.quiz.domain.valueobject.QuizDifficulty;

public record UpdateQuestionCommand(
        @NotBlank @Size(max = 500) String title,
        String description,
        @NotNull QuizDifficulty difficulty,
        @Positive int points,
        @Positive @Max(300) int timeLimitSeconds) {
}
