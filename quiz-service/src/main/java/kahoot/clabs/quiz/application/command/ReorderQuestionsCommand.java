package kahoot.clabs.quiz.application.command;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ReorderQuestionsCommand(@NotEmpty List<@NotNull UUID> questionIds) {
}
