package kahoot.clabs.domain.exception;

import kahoot.clabs.domain.shared.DomainException;

public class EmailAlreadyRegisteredException extends DomainException {

    public EmailAlreadyRegisteredException(String email) {
        super("Email is already registered: " + email);
    }
}
