package kahoot.clabs.application.event;

public record UserPermissionPayload(
        String name,
        String module
) {
}
