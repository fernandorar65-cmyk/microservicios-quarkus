package kahoot.clabs.quiz.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnswerOptionCommand(
        @NotBlank @Size(max = 500) String text,
        @NotNull Boolean correct) {
}
