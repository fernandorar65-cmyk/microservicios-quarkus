package kahoot.clabs.infrastructure.integration;

import java.util.Set;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.port.out.integration.OrganizationMembershipPort;
import kahoot.clabs.infrastructure.seed.SeedIds;

@ApplicationScoped
public class OrganizationMembershipStubAdapter implements OrganizationMembershipPort {

    private static final Set<UUID> DEMO_MEMBERS = Set.of(
            SeedIds.USER_ADMIN,
            SeedIds.USER_OWNER,
            SeedIds.USER_RH,
            SeedIds.USER_MEMBER);

    @Override
    public boolean organizationExists(UUID organizationId) {
        return SeedIds.ORG_CLABS.equals(organizationId);
    }

    @Override
    public boolean isActiveMember(UUID organizationId, UUID userId) {
        return organizationExists(organizationId) && DEMO_MEMBERS.contains(userId);
    }
}
