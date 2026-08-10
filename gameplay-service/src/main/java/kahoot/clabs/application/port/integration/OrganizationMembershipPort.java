package kahoot.clabs.application.port.integration;

import java.util.UUID;

/*Anti-corruption port for organization membership checks required by gameplay.*/
public interface OrganizationMembershipPort {

    boolean organizationExists(UUID organizationId);
    boolean isActiveMember(UUID organizationId, UUID userId);
}
