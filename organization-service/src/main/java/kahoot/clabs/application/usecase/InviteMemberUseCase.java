package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import kahoot.clabs.application.command.InviteMemberCommand;
import kahoot.clabs.application.dto.OrganizationResponse;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;
import kahoot.clabs.application.event.OrganizationProjectionSnapshot;
import kahoot.clabs.application.port.integration.OrganizationEventPublisher;
import kahoot.clabs.application.port.integration.UserDirectoryPort;
import kahoot.clabs.domain.aggregate.Organization;
import kahoot.clabs.domain.exception.OrganizationNotFoundException;
import kahoot.clabs.domain.repository.OrganizationRepository;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class InviteMemberUseCase {

    @Inject
    OrganizationRepository organizationRepository;

    @Inject
    UserDirectoryPort userDirectoryPort;

    @Inject
    OrganizationEventPublisher organizationEventPublisher;

    @Transactional
    public OrganizationResponse execute(UUID organizationId, InviteMemberCommand command) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        UUID userId = userDirectoryPort.findUserIdByEmail(command.email())
                .orElseThrow(() -> new DomainException("User not found: " + command.email()));
        UUID roleId = userDirectoryPort.findRoleIdByType(command.roleType())
                .orElseThrow(() -> new DomainException("Role not found: " + command.roleType()));

        organization.inviteMember(userId, roleId);
        Organization saved = organizationRepository.save(organization);
        organizationEventPublisher.publish(
                OrganizationIntegrationEvent.memberInvited(OrganizationProjectionSnapshot.from(saved)));
        return OrganizationResponse.from(saved);
    }
}
