package kahoot.clabs.infrastructure.adapter.organization;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.port.integration.OrganizationMembershipPort;

/**
 * Temporary stub until organization REST/Kafka integration exists.
 * In %dev, {@code app.membership.stub.allow-all=true} so seeded sessions are usable via API.
 */
@ApplicationScoped
public class OrganizationMembershipStubAdapter implements OrganizationMembershipPort {

    @ConfigProperty(name = "app.membership.stub.allow-all", defaultValue = "false")
    boolean allowAll;

    @Override
    public boolean organizationExists(UUID organizationId) {
        return allowAll && organizationId != null;
    }

    @Override
    public boolean isActiveMember(UUID organizationId, UUID userId) {
        return allowAll && organizationId != null && userId != null;
    }
}
