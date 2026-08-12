package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.UUID;

public record PermissionUpsertedEvent(
        UUID eventId,
        String eventType,
        int version,
        Instant occurredAt,
        UUID aggregateId,
        PermissionProjectionSnapshot payload
) {

    public static final int VERSION = 1;
    public static final String PERMISSION_UPSERTED = "PermissionUpserted";

    public static PermissionUpsertedEvent of(PermissionProjectionSnapshot payload) {
        return new PermissionUpsertedEvent(
                UUID.randomUUID(),
                PERMISSION_UPSERTED,
                VERSION,
                Instant.now(),
                payload.permissionId(),
                payload);
    }
}
