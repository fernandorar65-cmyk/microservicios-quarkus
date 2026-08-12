package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.UUID;

public record RoleUpsertedEvent(
        UUID eventId,
        String eventType,
        int version,
        Instant occurredAt,
        UUID aggregateId,
        RoleProjectionSnapshot payload
) {

    public static final int VERSION = 1;
    public static final String ROLE_UPSERTED = "RoleUpserted";

    public static RoleUpsertedEvent of(RoleProjectionSnapshot payload) {
        return new RoleUpsertedEvent(
                UUID.randomUUID(),
                ROLE_UPSERTED,
                VERSION,
                Instant.now(),
                payload.roleId(),
                payload);
    }
}
