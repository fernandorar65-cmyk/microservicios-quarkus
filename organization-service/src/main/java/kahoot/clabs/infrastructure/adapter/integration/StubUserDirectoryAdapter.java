package kahoot.clabs.infrastructure.adapter.integration;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.port.out.integration.UserDirectoryPort;
import kahoot.clabs.infrastructure.seed.SeedIds;

/**
 * Local-demo identity directory until identity-service REST integration is wired.
 * Resolves only well-known seed emails/roles.
 */
@ApplicationScoped
public class StubUserDirectoryAdapter implements UserDirectoryPort {

    private static final Map<String, UUID> DEMO_USERS = Map.of(
            "admin@kahoot-clabs.local", SeedIds.USER_ADMIN,
            "owner@kahoot-clabs.local", SeedIds.USER_OWNER,
            "rh@kahoot-clabs.local", SeedIds.USER_RH,
            "member@kahoot-clabs.local", SeedIds.USER_MEMBER);

    private static final Map<String, UUID> DEMO_ROLES = Map.of(
            "ADMIN", SeedIds.ROLE_ADMIN,
            "OWNER", SeedIds.ROLE_OWNER,
            "RH", SeedIds.ROLE_RH,
            "MEMBER", SeedIds.ROLE_MEMBER);

    @Override
    public Optional<UUID> findUserIdByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(DEMO_USERS.get(email.trim().toLowerCase(Locale.ROOT)));
    }

    @Override
    public Optional<UUID> findRoleIdByType(String roleType) {
        if (roleType == null || roleType.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(DEMO_ROLES.get(roleType.trim().toUpperCase(Locale.ROOT)));
    }
}
