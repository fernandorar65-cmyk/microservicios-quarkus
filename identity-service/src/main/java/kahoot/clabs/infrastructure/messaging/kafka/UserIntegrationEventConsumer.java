package kahoot.clabs.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.PermissionProjectionSnapshot;
import kahoot.clabs.application.event.PermissionUpsertedEvent;
import kahoot.clabs.application.event.RoleProjectionSnapshot;
import kahoot.clabs.application.event.RoleUpsertedEvent;
import kahoot.clabs.application.event.UserIntegrationEvent;
import kahoot.clabs.application.port.write.PermissionProjectionPort;
import kahoot.clabs.application.port.write.RoleProjectionPort;
import kahoot.clabs.application.port.write.UserProjectionPort;
import kahoot.clabs.application.readmodel.PermissionReadModel;
import kahoot.clabs.application.readmodel.RolePermissionReadModel;
import kahoot.clabs.application.readmodel.RoleReadModel;
import kahoot.clabs.application.readmodel.UserReadModels;

/**
 * Projects identity read models in Mongo. Runs outside the JPA write transaction
 * (Kafka consumer thread) so standalone Mongo is never enlisted in Narayana/JTA.
 */
@ApplicationScoped
public class UserIntegrationEventConsumer {

    private static final Logger LOG = Logger.getLogger(UserIntegrationEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final UserProjectionPort userProjectionPort;
    private final PermissionProjectionPort permissionProjectionPort;
    private final RoleProjectionPort roleProjectionPort;

    @Inject
    public UserIntegrationEventConsumer(
            ObjectMapper objectMapper,
            UserProjectionPort userProjectionPort,
            PermissionProjectionPort permissionProjectionPort,
            RoleProjectionPort roleProjectionPort) {
        this.objectMapper = objectMapper;
        this.userProjectionPort = userProjectionPort;
        this.permissionProjectionPort = permissionProjectionPort;
        this.roleProjectionPort = roleProjectionPort;
    }

    @Incoming("user-events-in")
    public void consume(JsonNode event) {
        if (event == null || event.isNull() || !event.hasNonNull("eventType")) {
            LOG.warn("Ignoring empty identity integration message");
            return;
        }

        String eventType = event.get("eventType").asText();
        switch (eventType) {
            case PermissionUpsertedEvent.PERMISSION_UPSERTED -> projectPermission(event);
            case RoleUpsertedEvent.ROLE_UPSERTED -> projectRole(event);
            case UserIntegrationEvent.USER_CREATED,
                    UserIntegrationEvent.USER_PROFILE_UPDATED,
                    UserIntegrationEvent.USER_ROLE_ASSIGNED,
                    UserIntegrationEvent.USER_LOGGED_IN -> projectUser(event);
            default -> LOG.warnf("Ignoring unknown identity eventType=%s", eventType);
        }
    }

    private void projectUser(JsonNode event) {
        UserIntegrationEvent userEvent = objectMapper.convertValue(event, UserIntegrationEvent.class);
        if (userEvent.payload() == null || userEvent.aggregateId() == null) {
            LOG.warn("Ignoring user event without payload");
            return;
        }
        userProjectionPort.save(UserReadModels.from(userEvent.payload()));
        LOG.infof(
                "Projected %s userId=%s eventId=%s",
                userEvent.eventType(),
                userEvent.aggregateId(),
                userEvent.eventId());
    }

    private void projectPermission(JsonNode event) {
        PermissionUpsertedEvent permissionEvent =
                objectMapper.convertValue(event, PermissionUpsertedEvent.class);
        PermissionProjectionSnapshot payload = permissionEvent.payload();
        if (payload == null) {
            LOG.warn("Ignoring permission event without payload");
            return;
        }
        PermissionReadModel model = new PermissionReadModel();
        model.setId(payload.permissionId());
        model.setName(payload.name());
        model.setDescription(payload.description());
        model.setModule(payload.module());
        model.setCreatedAt(payload.createdAt());
        model.setUpdatedAt(payload.updatedAt());
        permissionProjectionPort.save(model);
        LOG.infof(
                "Projected %s permissionId=%s eventId=%s",
                permissionEvent.eventType(),
                permissionEvent.aggregateId(),
                permissionEvent.eventId());
    }

    private void projectRole(JsonNode event) {
        RoleUpsertedEvent roleEvent = objectMapper.convertValue(event, RoleUpsertedEvent.class);
        RoleProjectionSnapshot payload = roleEvent.payload();
        if (payload == null) {
            LOG.warn("Ignoring role event without payload");
            return;
        }
        RoleReadModel model = new RoleReadModel();
        model.setId(payload.roleId());
        model.setName(payload.name());
        model.setType(payload.type());
        model.setDescription(payload.description());
        model.setCreatedAt(payload.createdAt());
        model.setUpdatedAt(payload.updatedAt());
        if (payload.permissions() != null) {
            model.setPermissions(payload.permissions().stream().map(permission -> {
                RolePermissionReadModel item = new RolePermissionReadModel();
                item.setId(permission.permissionId());
                item.setName(permission.name());
                item.setDescription(permission.description());
                item.setModule(permission.module());
                return item;
            }).toList());
        }
        roleProjectionPort.save(model);
        LOG.infof(
                "Projected %s roleId=%s eventId=%s",
                roleEvent.eventType(),
                roleEvent.aggregateId(),
                roleEvent.eventId());
    }
}
