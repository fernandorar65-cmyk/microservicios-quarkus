package kahoot.clabs.quiz.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateQuizCommand(
        @NotBlank @Size(min = 3, max = 200) String title,
        @NotNull UUID createdById) {
}
