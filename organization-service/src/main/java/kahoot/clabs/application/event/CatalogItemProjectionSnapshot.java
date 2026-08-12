package kahoot.clabs.application.event;

import java.util.UUID;

/**
 * Catalog entry payload for organization.events (statuses, departments, jobs).
 */
public record CatalogItemProjectionSnapshot(
        UUID id,
        String name,
        String description,
        String catalogKind
) {

    public static final String KIND_STATUS = "STATUS";
    public static final String KIND_MEMBER_STATUS = "MEMBER_STATUS";
    public static final String KIND_DEPARTMENT = "DEPARTMENT";
    public static final String KIND_JOB = "JOB";
}
