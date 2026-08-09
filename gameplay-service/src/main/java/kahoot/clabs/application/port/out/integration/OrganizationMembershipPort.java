package kahoot.clabs.application.port.out.integration;

import java.util.UUID;

/**
 * Anti-corruption port for organization membership checks required by gameplay.
 */
public interface OrganizationMembershipPort {

    boolean organizationExists(UUID organizationId);

    /**
     * Whether the user is associated with the organization (any non-absent membership).
     */
    boolean isActiveMember(UUID organizationId, UUID userId);
}
