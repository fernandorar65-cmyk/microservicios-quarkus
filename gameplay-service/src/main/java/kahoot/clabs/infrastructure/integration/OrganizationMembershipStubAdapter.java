package kahoot.clabs.infrastructure.integration;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.port.out.integration.OrganizationMembershipPort;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class OrganizationMembershipStubAdapter implements OrganizationMembershipPort {

    @Override
    public boolean organizationExists(UUID organizationId) {
        throw new DomainException(
                "Organization membership integration pending — cannot verify organization: " + organizationId);
    }

    @Override
    public boolean isActiveMember(UUID organizationId, UUID userId) {
        throw new DomainException(
                "Organization membership integration pending — cannot verify member "
                        + userId + " in organization " + organizationId);
    }
}
