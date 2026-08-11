package kahoot.clabs.application.port.integration;

import kahoot.clabs.application.event.GameSessionIntegrationEvent;

/**
 * Publishes game-session integration events (Kafka / other brokers).
 */
public interface GameSessionEventPublisher {

    void publish(GameSessionIntegrationEvent event);
}
