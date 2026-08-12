package kahoot.clabs.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class OrganizationIntegrationEventDeserializer extends ObjectMapperDeserializer<JsonNode> {

    public OrganizationIntegrationEventDeserializer() {
        super(JsonNode.class);
    }
}
