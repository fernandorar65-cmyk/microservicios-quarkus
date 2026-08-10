package kahoot.clabs.infrastructure.adapter.organization;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.port.integration.OrganizationMembershipPort;

@ApplicationScoped
public class OrganizationMembershipStubAdapter implements OrganizationMembershipPort {

    @Override
    public boolean organizationExists(UUID organizationId) {
        return false;
    }

    @Override
    public boolean isActiveMember(UUID organizationId, UUID userId) {
        return false;
    }
}
