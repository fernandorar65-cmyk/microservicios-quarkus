package kahoot.clabs.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class IdentityIntegrationEventDeserializer extends ObjectMapperDeserializer<JsonNode> {

    public IdentityIntegrationEventDeserializer() {
        super(JsonNode.class);
    }

    /** Used by Jackson ObjectMapperDeserializer; keep no-arg constructor above. */
    @SuppressWarnings("unused")
    private IdentityIntegrationEventDeserializer(ObjectMapper mapper) {
        super(JsonNode.class);
    }
}
