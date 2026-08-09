package kahoot.clabs.quiz.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryCommand(
        @NotNull UUID organizationId,
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description,
        @Size(max = 20) String color,
        @Size(max = 50) String icon) {
}
