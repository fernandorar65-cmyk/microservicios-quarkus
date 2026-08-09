package kahoot.clabs.domain.exception;

import java.util.UUID;

import kahoot.clabs.domain.shared.DomainException;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(UUID userId) {
        super("User not found: " + userId);
    }

    public UserNotFoundException(String email) {
        super("User not found: " + email);
    }
}
