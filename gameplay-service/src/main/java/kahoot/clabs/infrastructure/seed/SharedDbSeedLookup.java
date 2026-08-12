package kahoot.clabs.infrastructure.seed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot.AnswerOptionSnapshot;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot.QuestionSnapshot;

/**
 * Temporary shared-DB lookups for local seed (same kahoot_db as identity/org/quiz).
 * Seed reads must use Postgres/JDBC here — never Mongo inside a JPA {@code @Transactional}.
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

    /**
     * SEED / MIGRATION ONLY — builds freeze payload from Postgres quiz write model.
     * Do not call Mongo from seed: standalone Mongo + active JTA causes
     * "Transaction numbers are only allowed on a replica set member or mongos".
     * Runtime playable snapshots still come from Kafka → Mongo projection.
     */
    public Optional<PublishedQuizSnapshot> loadPublishedQuizSnapshotForSeed(
            UUID organizationId, UUID quizId) {
        if (organizationId == null || quizId == null) {
            return Optional.empty();
        }

        String quizSql = """
                SELECT id, organization_id
                FROM quizzes
                WHERE id = ? AND organization_id = ? AND status = 'PUBLISHED'
                """;
        String questionsSql = """
                SELECT q.id AS question_id,
                       q.order_index,
                       q.points,
                       q.time_limit_seconds,
                       q.title,
                       q.description,
                       q.type,
                       a.id AS option_id,
                       a.text AS option_text,
                       a.is_correct,
                       a.order_index AS option_order
                FROM questions q
                LEFT JOIN answer_options a ON a.question_id = q.id
                WHERE q.quiz_id = ?
                ORDER BY q.order_index, a.order_index
                """;

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement quizStatement = connection.prepareStatement(quizSql)) {
                quizStatement.setObject(1, quizId);
                quizStatement.setObject(2, organizationId);
                try (ResultSet quizRs = quizStatement.executeQuery()) {
                    if (!quizRs.next()) {
                        return Optional.empty();
                    }
                }
            }

            Map<UUID, QuestionBuilder> questions = new LinkedHashMap<>();
            try (PreparedStatement questionsStatement = connection.prepareStatement(questionsSql)) {
                questionsStatement.setObject(1, quizId);
                try (ResultSet rs = questionsStatement.executeQuery()) {
                    while (rs.next()) {
                        UUID questionId = rs.getObject("question_id", UUID.class);
                        int orderIndex = rs.getInt("order_index");
                        int points = rs.getInt("points");
                        int timeLimitSeconds = rs.getInt("time_limit_seconds");
                        String title = rs.getString("title");
                        String description = rs.getString("description");
                        String type = rs.getString("type");

                        QuestionBuilder question = questions.computeIfAbsent(
                                questionId,
                                id -> new QuestionBuilder(
                                        id,
                                        orderIndex,
                                        points,
                                        timeLimitSeconds,
                                        title,
                                        description,
                                        type));

                        UUID optionId = rs.getObject("option_id", UUID.class);
                        if (optionId != null) {
                            String optionText = rs.getString("option_text");
                            boolean correct = rs.getBoolean("is_correct");
                            int optionOrder = rs.getInt("option_order");
                            question.options.add(new AnswerOptionSnapshot(
                                    optionId, optionText, correct, optionOrder));
                        }
                    }
                }
            }

            if (questions.isEmpty()) {
                return Optional.empty();
            }

            List<QuestionSnapshot> snapshotQuestions = questions.values().stream()
                    .map(QuestionBuilder::toSnapshot)
                    .toList();
            return Optional.of(new PublishedQuizSnapshot(quizId, organizationId, snapshotQuestions));
        } catch (Exception ex) {
            throw new IllegalStateException("Seed published quiz snapshot lookup failed", ex);
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

    private static final class QuestionBuilder {
        private final UUID id;
        private final int orderIndex;
        private final int points;
        private final int timeLimitSeconds;
        private final String title;
        private final String description;
        private final String type;
        private final List<AnswerOptionSnapshot> options = new ArrayList<>();

        private QuestionBuilder(
                UUID id,
                int orderIndex,
                int points,
                int timeLimitSeconds,
                String title,
                String description,
                String type) {
            this.id = id;
            this.orderIndex = orderIndex;
            this.points = points;
            this.timeLimitSeconds = timeLimitSeconds;
            this.title = title;
            this.description = description;
            this.type = type;
        }

        private QuestionSnapshot toSnapshot() {
            return new QuestionSnapshot(
                    id,
                    orderIndex,
                    points,
                    timeLimitSeconds,
                    title,
                    description,
                    type,
                    List.copyOf(options));
        }
    }
}
