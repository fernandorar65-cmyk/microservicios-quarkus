package kahoot.clabs.domain.event;

import java.util.UUID;

import kahoot.clabs.domain.shared.DomainEvent;

/**
 * Parked domain event for a future user-created pipeline.
 * Not registered in MVP: {@code User.create} still uses a null id until persistence assigns one.
 *
 * @see docs/domain-events.md
 */
public class UserCreatedEvent extends DomainEvent {

    private final UUID userId;
    private final String email;

    public UserCreatedEvent(UUID userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
