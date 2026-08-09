package kahoot.clabs.domain.exception;

import java.util.UUID;

import kahoot.clabs.domain.valueobject.RoleType;
import kahoot.clabs.domain.shared.DomainException;

public class RoleNotFoundException extends DomainException {

    public RoleNotFoundException(UUID roleId) {
        super("Role not found: " + roleId);
    }

    public RoleNotFoundException(RoleType type) {
        super("Role not found: " + type);
    }
}
