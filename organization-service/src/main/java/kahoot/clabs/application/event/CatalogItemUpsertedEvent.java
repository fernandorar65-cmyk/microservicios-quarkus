package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.UUID;

public record CatalogItemUpsertedEvent(
        UUID eventId,
        String eventType,
        int version,
        Instant occurredAt,
        UUID aggregateId,
        CatalogItemProjectionSnapshot payload
) {

    public static final int VERSION = 1;
    public static final String CATALOG_ITEM_UPSERTED = "CatalogItemUpserted";

    public static CatalogItemUpsertedEvent of(CatalogItemProjectionSnapshot payload) {
        return new CatalogItemUpsertedEvent(
                UUID.randomUUID(),
                CATALOG_ITEM_UPSERTED,
                VERSION,
                Instant.now(),
                payload.id(),
                payload);
    }
}
