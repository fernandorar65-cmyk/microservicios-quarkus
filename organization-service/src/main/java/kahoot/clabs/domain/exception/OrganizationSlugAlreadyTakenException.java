package kahoot.clabs.domain.exception;

import kahoot.clabs.domain.shared.DomainException;

public class OrganizationSlugAlreadyTakenException extends DomainException {

    public OrganizationSlugAlreadyTakenException(String slug) {
        super("Organization slug is already taken: " + slug);
    }
}
