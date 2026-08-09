package kahoot.clabs.application.command;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

public record UpdateProfileCommand(
        @Size(max = 30) String phoneNumber,
        LocalDate birthDate,
        String bio,
        @Size(max = 150) String location
) {
}
