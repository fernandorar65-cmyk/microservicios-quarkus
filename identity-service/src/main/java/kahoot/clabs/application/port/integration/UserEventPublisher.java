package kahoot.clabs.application.port.integration;

import kahoot.clabs.application.event.PermissionUpsertedEvent;
import kahoot.clabs.application.event.RoleUpsertedEvent;
import kahoot.clabs.application.event.UserIntegrationEvent;

public interface UserEventPublisher {

    void publish(UserIntegrationEvent event);

    void publish(PermissionUpsertedEvent event);

    void publish(RoleUpsertedEvent event);
}
