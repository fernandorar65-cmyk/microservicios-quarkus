package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.Optional;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.postgres.entity.GameSessionJpaEntity;

/**
 * Write-side only. Session list/get/leaderboard queries belong on Mongo read ports.
 * {@link #findByIdWithDetails} exists solely to rehydrate the aggregate for commands.
 */
@ApplicationScoped
public class GameSessionPanacheRepository implements PanacheRepositoryBase<GameSessionJpaEntity, UUID> {

    private static final String AGGREGATE_FETCH = """
            select distinct s
            from GameSessionJpaEntity s
            left join fetch s.players
            left join fetch s.questions questions
            left join fetch questions.answerOptions
            """;

    /** Command rehydration of the GameSession aggregate graph. */
    public Optional<GameSessionJpaEntity> findByIdWithDetails(UUID id) {
        return find(AGGREGATE_FETCH + " where s.id = ?1", id).firstResultOptional();
    }
}
