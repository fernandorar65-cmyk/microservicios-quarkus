package kahoot.clabs.application.port.integration;

import kahoot.clabs.application.event.OrganizationIntegrationEvent;

/**
 * Publishes organization integration events (Kafka in infrastructure).
 */
public interface OrganizationEventPublisher {

    void publish(OrganizationIntegrationEvent event);
}
