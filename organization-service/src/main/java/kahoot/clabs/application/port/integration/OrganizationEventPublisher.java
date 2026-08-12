package kahoot.clabs.application.port.integration;

import kahoot.clabs.application.event.CatalogItemUpsertedEvent;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;

public interface OrganizationEventPublisher {

    void publish(OrganizationIntegrationEvent event);

    void publish(CatalogItemUpsertedEvent event);
}
