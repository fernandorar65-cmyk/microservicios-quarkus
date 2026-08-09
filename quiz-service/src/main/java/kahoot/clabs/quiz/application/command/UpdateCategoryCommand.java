package kahoot.clabs.quiz.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryCommand(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description,
        @Size(max = 20) String color,
        @Size(max = 50) String icon) {
}
