package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import kahoot.clabs.application.command.UpdateOrganizationCommand;
import kahoot.clabs.application.dto.OrganizationResponse;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;
import kahoot.clabs.application.event.OrganizationProjectionSnapshot;
import kahoot.clabs.application.port.integration.OrganizationEventPublisher;
import kahoot.clabs.domain.aggregate.Organization;
import kahoot.clabs.domain.exception.OrganizationNotFoundException;
import kahoot.clabs.domain.repository.OrganizationRepository;

@ApplicationScoped
public class UpdateOrganizationUseCase {

    @Inject
    OrganizationRepository organizationRepository;

    @Inject
    OrganizationEventPublisher organizationEventPublisher;

    @Transactional
    public OrganizationResponse execute(UUID organizationId, UpdateOrganizationCommand command) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        organization.updateDetails(command.name(), command.description());
        Organization saved = organizationRepository.save(organization);
        organizationEventPublisher.publish(
                OrganizationIntegrationEvent.organizationUpdated(OrganizationProjectionSnapshot.from(saved)));
        return OrganizationResponse.from(saved);
    }
}
