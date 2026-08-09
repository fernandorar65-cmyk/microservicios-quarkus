package kahoot.clabs.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record OpenQuestionCommand(
        @NotNull UUID hostUserId,
        Integer questionIndex) {
}
