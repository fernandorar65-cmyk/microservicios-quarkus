package kahoot.clabs.infrastructure.adapter.integration;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.port.out.integration.UserDirectoryPort;
import kahoot.clabs.domain.shared.DomainException;

/**
 * Temporary stub until identity-service REST integration is wired.
 * Replace with RestUserDirectoryAdapter calling identity-service endpoints.
 */
@ApplicationScoped
public class StubUserDirectoryAdapter implements UserDirectoryPort {

    private static final String MESSAGE = "Identity integration pending";

    @Override
    public Optional<UUID> findUserIdByEmail(String email) {
        throw identityPending();
    }

    @Override
    public Optional<UUID> findRoleIdByType(String roleType) {
        throw identityPending();
    }

    private DomainException identityPending() {
        return new DomainException(MESSAGE, new UnsupportedOperationException(MESSAGE));
    }
}
