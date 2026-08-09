package kahoot.clabs.application.dto;

import java.util.UUID;

import kahoot.clabs.domain.aggregate.User;

public record AuthUserResponse(
        UUID userId,
        String email,
        String firstName,
        String lastName
) {

    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
                user.getId(),
                user.getEmail().value(),
                user.getFullName().firstName(),
                user.getFullName().lastName());
    }
}
