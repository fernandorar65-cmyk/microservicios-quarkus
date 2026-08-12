package kahoot.clabs.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;

public class OrganizationIntegrationEventDeserializer
        extends ObjectMapperDeserializer<OrganizationIntegrationEvent> {

    public OrganizationIntegrationEventDeserializer() {
        super(OrganizationIntegrationEvent.class);
    }
}
