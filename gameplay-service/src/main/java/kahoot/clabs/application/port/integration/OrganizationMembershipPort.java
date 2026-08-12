package kahoot.clabs.application.port.integration;

import java.util.UUID;

public interface OrganizationMembershipPort {

    boolean organizationExists(UUID organizationId);
    boolean isActiveMember(UUID organizationId, UUID userId);
}
