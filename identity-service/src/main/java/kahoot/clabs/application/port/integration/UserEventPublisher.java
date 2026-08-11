package kahoot.clabs.application.port.integration;

import kahoot.clabs.application.event.UserIntegrationEvent;

/**
 * Publishes user-related integration events (Kafka / other brokers).
 */
public interface UserEventPublisher {

    void publish(UserIntegrationEvent event);
}
