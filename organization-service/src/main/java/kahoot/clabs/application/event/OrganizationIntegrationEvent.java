package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Versionable Kafka envelope for organization.events.
 * Payload is always a projection snapshot.
 */
public record OrganizationIntegrationEvent(
        UUID eventId,
        String eventType,
        int version,
        Instant occurredAt,
        UUID aggregateId,
        OrganizationProjectionSnapshot payload
) {

    public static final int VERSION = 1;

    public static final String ORGANIZATION_CREATED = "OrganizationCreated";
    public static final String ORGANIZATION_UPDATED = "OrganizationUpdated";
    public static final String ORGANIZATION_MEMBER_INVITED = "OrganizationMemberInvited";
    public static final String ORGANIZATION_DELETED = "OrganizationDeleted";

    public static OrganizationIntegrationEvent of(String eventType, OrganizationProjectionSnapshot payload) {
        Instant now = Instant.now();
        return new OrganizationIntegrationEvent(
                UUID.randomUUID(),
                eventType,
                VERSION,
                now,
                payload.organizationId(),
                payload);
    }

    public static OrganizationIntegrationEvent organizationCreated(OrganizationProjectionSnapshot payload) {
        return of(ORGANIZATION_CREATED, payload);
    }

    public static OrganizationIntegrationEvent organizationUpdated(OrganizationProjectionSnapshot payload) {
        return of(ORGANIZATION_UPDATED, payload);
    }

    public static OrganizationIntegrationEvent memberInvited(OrganizationProjectionSnapshot payload) {
        return of(ORGANIZATION_MEMBER_INVITED, payload);
    }

    public static OrganizationIntegrationEvent organizationDeleted(UUID organizationId) {
        Instant now = Instant.now();
        return new OrganizationIntegrationEvent(
                UUID.randomUUID(),
                ORGANIZATION_DELETED,
                VERSION,
                now,
                organizationId,
                null);
    }
}
