package kahoot.clabs.application.port.integration;

import java.util.Optional;
import java.util.UUID;

/**
 * Anti-corruption port for identity lookups required by organization.
 * Organization must not depend on User/Role aggregates or identity write repositories.
 */
public interface UserDirectoryPort {

    Optional<UUID> findUserIdByEmail(String email);

    Optional<UUID> findRoleIdByType(String roleType);
}
