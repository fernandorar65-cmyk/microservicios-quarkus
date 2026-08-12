package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.UUID;

public record OrganizationMemberPayload(
        UUID id,
        UUID userId,
        UUID roleId,
        String status,
        Instant joinedAt
) {
}
