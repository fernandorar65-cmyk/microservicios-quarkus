package kahoot.clabs.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrganizationCommand(
        @NotBlank @Size(min = 2, max = 150) String name,
        @NotBlank @Size(min = 2, max = 100) String slug,
        @Size(max = 2000) String description) {
}
