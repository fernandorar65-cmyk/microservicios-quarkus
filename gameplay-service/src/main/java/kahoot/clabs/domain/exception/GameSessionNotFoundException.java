package kahoot.clabs.domain.exception;

import java.util.UUID;

import kahoot.clabs.domain.shared.DomainException;

public class GameSessionNotFoundException extends DomainException {

    public GameSessionNotFoundException(UUID sessionId) {
        super("Game session not found: " + sessionId);
    }
}
