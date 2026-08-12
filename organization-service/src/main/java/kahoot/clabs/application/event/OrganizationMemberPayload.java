package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Member data needed to project the organization read model.
 */
public record OrganizationMemberPayload(
        UUID id,
        UUID userId,
        UUID roleId,
        String status,
        Instant joinedAt
) {
}
