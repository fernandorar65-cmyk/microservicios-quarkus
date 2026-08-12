package kahoot.clabs.application.event;

import java.time.Instant;
import java.util.UUID;

public record UserIntegrationEvent(
        UUID eventId,
        String eventType,
        int version,
        Instant occurredAt,
        UUID aggregateId,
        UserProjectionSnapshot payload
) {

    public static final int VERSION = 1;

    public static final String USER_CREATED = "UserCreated";
    public static final String USER_PROFILE_UPDATED = "UserProfileUpdated";
    public static final String USER_ROLE_ASSIGNED = "UserRoleAssigned";
    public static final String USER_LOGGED_IN = "UserLoggedIn";

    public static UserIntegrationEvent of(String eventType, UserProjectionSnapshot payload) {
        Instant now = Instant.now();
        return new UserIntegrationEvent(
                UUID.randomUUID(),
                eventType,
                VERSION,
                now,
                payload.userId(),
                payload);
    }

    public static UserIntegrationEvent userCreated(UserProjectionSnapshot payload) {
        return of(USER_CREATED, payload);
    }

    public static UserIntegrationEvent profileUpdated(UserProjectionSnapshot payload) {
        return of(USER_PROFILE_UPDATED, payload);
    }

    public static UserIntegrationEvent roleAssigned(UserProjectionSnapshot payload) {
        return of(USER_ROLE_ASSIGNED, payload);
    }

    public static UserIntegrationEvent loggedIn(UserProjectionSnapshot payload) {
        return of(USER_LOGGED_IN, payload);
    }
}
