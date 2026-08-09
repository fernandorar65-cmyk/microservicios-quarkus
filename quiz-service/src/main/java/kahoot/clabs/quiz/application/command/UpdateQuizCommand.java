package kahoot.clabs.quiz.application.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kahoot.clabs.quiz.domain.valueobject.QuizDifficulty;

public record UpdateQuizCommand(
        @NotBlank @Size(min = 3, max = 200) String title,
        String description,
        @NotNull QuizDifficulty difficulty,
        @NotNull @Positive @Max(180) Long estimatedTimeMinutes,
        @NotNull @Valid QuizSettingsCommand settings) {
}
