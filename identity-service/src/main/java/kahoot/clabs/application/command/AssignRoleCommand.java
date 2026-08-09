package kahoot.clabs.application.command;

import jakarta.validation.constraints.NotNull;
import kahoot.clabs.domain.valueobject.RoleType;

public record AssignRoleCommand(
        @NotNull RoleType roleType
) {
}
