package kahoot.clabs.application.port.integration;

import kahoot.clabs.application.event.CatalogItemUpsertedEvent;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;

/**
 * Publishes organization integration events to Kafka (never writes Mongo).
 */
public interface OrganizationEventPublisher {

    void publish(OrganizationIntegrationEvent event);

    void publish(CatalogItemUpsertedEvent event);
}
