package kahoot.clabs.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.CatalogItemProjectionSnapshot;
import kahoot.clabs.application.event.CatalogItemUpsertedEvent;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;
import kahoot.clabs.application.port.write.OrganizationProjectionPort;
import kahoot.clabs.application.readmodel.OrganizationReadModels;
import kahoot.clabs.infrastructure.persistence.mongo.adapter.OrganizationCatalogProjectionAdapter;

/**
 * Projects organization / catalog read models in Mongo outside any JPA write transaction.
 */
@ApplicationScoped
public class OrganizationIntegrationEventConsumer {

    private static final Logger LOG = Logger.getLogger(OrganizationIntegrationEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final OrganizationProjectionPort organizationProjectionPort;
    private final OrganizationCatalogProjectionAdapter catalogProjectionAdapter;

    @Inject
    public OrganizationIntegrationEventConsumer(
            ObjectMapper objectMapper,
            OrganizationProjectionPort organizationProjectionPort,
            OrganizationCatalogProjectionAdapter catalogProjectionAdapter) {
        this.objectMapper = objectMapper;
        this.organizationProjectionPort = organizationProjectionPort;
        this.catalogProjectionAdapter = catalogProjectionAdapter;
    }

    @Incoming("organization-events-in")
    public void consume(JsonNode event) {
        if (event == null || event.isNull() || !event.hasNonNull("eventType")) {
            LOG.warn("Ignoring empty organization integration message");
            return;
        }

        String eventType = event.get("eventType").asText();
        if (CatalogItemUpsertedEvent.CATALOG_ITEM_UPSERTED.equals(eventType)) {
            projectCatalog(event);
            return;
        }

        OrganizationIntegrationEvent orgEvent =
                objectMapper.convertValue(event, OrganizationIntegrationEvent.class);
        if (orgEvent.aggregateId() == null) {
            LOG.warn("Ignoring organization event without aggregateId");
            return;
        }

        if (OrganizationIntegrationEvent.ORGANIZATION_DELETED.equals(orgEvent.eventType())) {
            organizationProjectionPort.deleteById(orgEvent.aggregateId());
            LOG.infof(
                    "Projected %s organizationId=%s eventId=%s",
                    orgEvent.eventType(),
                    orgEvent.aggregateId(),
                    orgEvent.eventId());
            return;
        }

        if (orgEvent.payload() == null) {
            LOG.warnf("Ignoring %s without payload organizationId=%s", orgEvent.eventType(), orgEvent.aggregateId());
            return;
        }

        organizationProjectionPort.save(OrganizationReadModels.from(orgEvent.payload()));
        LOG.infof(
                "Projected %s organizationId=%s eventId=%s",
                orgEvent.eventType(),
                orgEvent.aggregateId(),
                orgEvent.eventId());
    }

    private void projectCatalog(JsonNode event) {
        CatalogItemUpsertedEvent catalogEvent =
                objectMapper.convertValue(event, CatalogItemUpsertedEvent.class);
        CatalogItemProjectionSnapshot payload = catalogEvent.payload();
        if (payload == null || payload.catalogKind() == null) {
            LOG.warn("Ignoring catalog event without payload");
            return;
        }
        if (isUnknownKind(payload.catalogKind())) {
            LOG.warnf("Unknown catalogKind=%s", payload.catalogKind());
            return;
        }
        catalogProjectionAdapter.upsert(payload);
        LOG.infof(
                "Projected %s kind=%s id=%s eventId=%s",
                catalogEvent.eventType(),
                payload.catalogKind(),
                payload.id(),
                catalogEvent.eventId());
    }

    private static boolean isUnknownKind(String kind) {
        return !CatalogItemProjectionSnapshot.KIND_STATUS.equals(kind)
                && !CatalogItemProjectionSnapshot.KIND_MEMBER_STATUS.equals(kind)
                && !CatalogItemProjectionSnapshot.KIND_DEPARTMENT.equals(kind)
                && !CatalogItemProjectionSnapshot.KIND_JOB.equals(kind);
    }
}
