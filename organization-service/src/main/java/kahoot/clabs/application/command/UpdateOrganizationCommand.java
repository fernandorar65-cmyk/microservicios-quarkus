package kahoot.clabs.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOrganizationCommand(
        @NotBlank @Size(min = 2, max = 150) String name,
        String description) {
}
