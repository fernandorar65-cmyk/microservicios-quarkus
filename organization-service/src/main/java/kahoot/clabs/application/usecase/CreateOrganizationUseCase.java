package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import kahoot.clabs.application.command.CreateOrganizationCommand;
import kahoot.clabs.application.dto.OrganizationResponse;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;
import kahoot.clabs.application.event.OrganizationProjectionSnapshot;
import kahoot.clabs.application.port.blob.AssetsStoragePort;
import kahoot.clabs.application.port.integration.OrganizationEventPublisher;
import kahoot.clabs.domain.aggregate.Organization;
import kahoot.clabs.domain.exception.OrganizationSlugAlreadyTakenException;
import kahoot.clabs.domain.repository.OrganizationRepository;
import kahoot.clabs.domain.shared.DomainException;
import kahoot.clabs.domain.valueobject.OrganizationSlug;

@ApplicationScoped
public class CreateOrganizationUseCase {

    private static final int MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

    @Inject
    OrganizationRepository organizationRepository;

    @Inject
    AssetsStoragePort assetsStoragePort;

    @Inject
    OrganizationEventPublisher organizationEventPublisher;

    @Transactional
    public OrganizationResponse execute(
            CreateOrganizationCommand command,
            byte[] logo,
            String contentType,
            String filename) {

        String slug = OrganizationSlug.of(command.slug()).value();
        if (organizationRepository.existsBySlug(slug)) {
            throw new OrganizationSlugAlreadyTakenException(slug);
        }

        Organization organization = Organization.create(command.name(), slug);
        if (command.description() != null && !command.description().isBlank()) {
            organization.updateDetails(command.name(), command.description().trim());
        }

        if (logo != null && logo.length > 0) {
            validateImage(logo, contentType);
            String key = "organizations/%s/logo/%s%s".formatted(
                    slug,
                    UUID.randomUUID(),
                    extension(filename, contentType));
            String logoUrl = assetsStoragePort.upload(key, logo, contentType);
            organization.changeLogo(logoUrl);
        }

        Organization saved = organizationRepository.save(organization);
        organizationEventPublisher.publish(
                OrganizationIntegrationEvent.organizationCreated(OrganizationProjectionSnapshot.from(saved)));
        return OrganizationResponse.from(saved);
    }

    private void validateImage(byte[] content, String contentType) {
        if (content.length > MAX_IMAGE_SIZE_BYTES) {
            throw new DomainException("Logo must be at most 5 MB");
        }
        if (!"image/jpeg".equals(contentType)
                && !"image/png".equals(contentType)
                && !"image/webp".equals(contentType)
                && !"image/gif".equals(contentType)) {
            throw new DomainException("Only JPEG, PNG, WebP, and GIF images are allowed");
        }
    }

    private String extension(String filename, String contentType) {
        if (filename != null && filename.lastIndexOf('.') >= 0) {
            return filename.substring(filename.lastIndexOf('.')).toLowerCase();
        }
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".gif";
        };
    }
}
