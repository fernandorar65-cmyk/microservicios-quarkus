package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Integration event: a user was registered. Payload for read-model projection (no secrets).
 */
public record UserCreatedEvent(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
