package kahoot.clabs.domain.exception;

import java.util.UUID;

import kahoot.clabs.domain.shared.DomainException;

public class OrganizationNotFoundException extends DomainException {

    public OrganizationNotFoundException(UUID organizationId) {
        super("Organization not found: " + organizationId);
    }

    public OrganizationNotFoundException(String slug) {
        super("Organization not found: " + slug);
    }
}
