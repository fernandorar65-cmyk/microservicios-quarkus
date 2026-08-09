package kahoot.clabs.quiz.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record DuplicateQuizCommand(@NotNull UUID createdById) {
}
