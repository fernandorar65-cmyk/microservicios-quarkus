package kahoot.clabs.infrastructure.adapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.port.integration.UserDirectoryPort;

/**
 * Temporary shared-DB lookup against identity tables in kahoot_db.
 * Replace with REST client when databases are split per service.
 */
@ApplicationScoped
public class UseDirectoryAdapter implements UserDirectoryPort {

    private final DataSource dataSource;

    @Inject
    public UseDirectoryAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<UUID> findUserIdByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        String sql = "SELECT id FROM users WHERE lower(email) = lower(?)";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.trim());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(rs.getObject(1, UUID.class));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to resolve user by email: " + email, ex);
        }
    }

    @Override
    public Optional<UUID> findRoleIdByType(String roleType) {
        if (roleType == null || roleType.isBlank()) {
            return Optional.empty();
        }
        String sql = "SELECT id FROM roles WHERE type = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleType.trim());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(rs.getObject(1, UUID.class));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to resolve role by type: " + roleType, ex);
        }
    }
}
