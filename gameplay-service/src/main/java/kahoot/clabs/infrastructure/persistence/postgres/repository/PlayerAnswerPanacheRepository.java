package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.infrastructure.persistence.postgres.entity.PlayerAnswerJpaEntity;

@ApplicationScoped
public class PlayerAnswerPanacheRepository implements PanacheRepositoryBase<PlayerAnswerJpaEntity, UUID> {


    public List<PlayerAnswerJpaEntity> findBySessionPlayerIdIn(Collection<UUID> playerIds) {
        if (playerIds == null || playerIds.isEmpty()) {
            return List.of();
        }
        return list("session_players.id in ?1", playerIds);
    }

    @Transactional
    public void deleteBySessionPlayerIdInAndIdNotIn(
            Collection<UUID> playerIds, Collection<UUID> answerIds) {
        if (playerIds == null || playerIds.isEmpty()) {
            return;
        }
        if (answerIds == null || answerIds.isEmpty()) {
            deleteBySessionPlayerIdIn(playerIds);
            return;
        }
        delete("session_players.id in ?1 and id not in ?2", playerIds, answerIds);
    }

    @Transactional
    public void deleteBySessionPlayerIdIn(Collection<UUID> playerIds) {
        if (playerIds == null || playerIds.isEmpty()) {
            return;
        }
        delete("session_players.id in ?1", playerIds);
    }
}
