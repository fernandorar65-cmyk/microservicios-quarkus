package kahoot.clabs.application.port.integration;

import java.util.Optional;
import java.util.UUID;

public interface UserDirectoryPort {

    Optional<UUID> findUserIdByEmail(String email);

    Optional<UUID> findRoleIdByType(String roleType);
}
