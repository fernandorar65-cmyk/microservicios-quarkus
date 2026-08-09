package kahoot.clabs.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JoinSessionCommand(
        @NotNull UUID userId,
        @NotBlank @Size(max = 30) String nickname) {
}
