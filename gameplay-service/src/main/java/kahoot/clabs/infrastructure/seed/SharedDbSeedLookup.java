package kahoot.clabs.infrastructure.seed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Temporary shared-DB lookups for local seed (same kahoot_db as identity/org/quiz).
 */
@ApplicationScoped
public class SharedDbSeedLookup {

    private final DataSource dataSource;

    @Inject
    public SharedDbSeedLookup(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<UUID> findOrganizationIdBySlug(String slug) {
        return findUuid("SELECT id FROM organizations WHERE slug = ?", slug);
    }

    public Optional<UUID> findUserIdByEmail(String email) {
        return findUuid("SELECT id FROM users WHERE lower(email) = lower(?)", email);
    }

    public Optional<UUID> findPublishedQuizId(UUID organizationId, String title) {
        if (organizationId == null || title == null || title.isBlank()) {
            return Optional.empty();
        }
        String sql = """
                SELECT id FROM quizzes
                WHERE organization_id = ?
                  AND lower(title) = lower(?)
                  AND status = 'PUBLISHED'
                """;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, organizationId);
            statement.setString(2, title.trim());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(rs.getObject(1, UUID.class));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Seed quiz lookup failed", ex);
        }
    }

    private Optional<UUID> findUuid(String sql, String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value.trim());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(rs.getObject(1, UUID.class));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Seed lookup failed: " + sql, ex);
        }
    }
}
