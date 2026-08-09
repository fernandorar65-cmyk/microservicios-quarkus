package kahoot.clabs.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record LeaveSessionCommand(@NotNull UUID userId) {
}
