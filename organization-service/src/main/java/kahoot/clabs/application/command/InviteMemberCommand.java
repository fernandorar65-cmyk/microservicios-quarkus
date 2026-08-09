package kahoot.clabs.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteMemberCommand(
        @NotBlank @Email String email,
        @NotBlank String roleType) {
}
