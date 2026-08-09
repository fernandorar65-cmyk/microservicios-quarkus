package kahoot.clabs.domain.exception;

import kahoot.clabs.domain.shared.DomainException;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
