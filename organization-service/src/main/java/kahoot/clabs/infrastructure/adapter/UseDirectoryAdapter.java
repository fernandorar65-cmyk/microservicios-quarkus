package kahoot.clabs.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.port.integration.UserDirectoryPort;

@ApplicationScoped
public class UseDirectoryAdapter implements UserDirectoryPort {

    @Override
    public Optional<UUID> findUserIdByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<UUID> findRoleIdByType(String roleType) {
        return Optional.empty();
    }
}
