package kahoot.clabs.application.port.integration;

import kahoot.clabs.application.event.GameSessionIntegrationEvent;

public interface GameSessionEventPublisher {

    void publish(GameSessionIntegrationEvent event);
}
