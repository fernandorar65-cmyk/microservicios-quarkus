package kahoot.clabs.application.event;

import java.util.List;
import java.util.UUID;

public record UserRolePayload(
        UUID id,
        String name,
        String type,
        List<UserPermissionPayload> permissions
) {
}
