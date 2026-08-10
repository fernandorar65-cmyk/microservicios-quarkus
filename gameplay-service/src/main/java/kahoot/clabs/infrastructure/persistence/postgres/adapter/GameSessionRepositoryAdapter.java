package kahoot.clabs.infrastructure.persistence.postgres.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.repository.GameSessionRepository;
import kahoot.clabs.infrastructure.persistence.postgres.entity.GameSessionJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.GameSessionPersistenceMapper;
import kahoot.clabs.infrastructure.persistence.postgres.entity.PlayerAnswerJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.SessionPlayerJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.repository.GameSessionPanacheRepository;
import kahoot.clabs.infrastructure.persistence.postgres.repository.PlayerAnswerPanacheRepository;

@ApplicationScoped
public class GameSessionRepositoryAdapter implements GameSessionRepository {

    @Inject
    GameSessionPanacheRepository sessionRepository;

    @Inject
    PlayerAnswerPanacheRepository answerRepository;

    @Override
    @Transactional
    public GameSession save(GameSession session) {
        GameSessionJpaEntity entity = GameSessionPersistenceMapper.toEntity(session);
        sessionRepository.getEntityManager().merge(entity);
        sessionRepository.flush();
        GameSessionJpaEntity saved = sessionRepository.findByIdWithDetails(session.getId()).orElseThrow();
        syncAnswers(session, saved);
        return toAggregate(saved);
    }

    @Override
    @Transactional
    public Optional<GameSession> findById(UUID id) {
        return sessionRepository.findByIdWithDetails(id).map(this::toAggregate);
    }

    @Override
    public boolean existsById(UUID id) {
        return sessionRepository.existsById(id);
    }

    private GameSession toAggregate(GameSessionJpaEntity entity) {
        return GameSessionPersistenceMapper.toDomain(entity, loadAnswers(entity));
    }

    private List<PlayerAnswerJpaEntity> loadAnswers(GameSessionJpaEntity entity) {
        List<UUID> playerIds = entity.getPlayers().stream()
                .map(SessionPlayerJpaEntity::getId)
                .toList();
        if (playerIds.isEmpty()) {
            return List.of();
        }
        return answerRepository.findBySessionPlayerIdIn(playerIds);
    }

    private void syncAnswers(GameSession session, GameSessionJpaEntity savedEntity) {
        Collection<UUID> playerIds = session.getPlayers().stream()
                .map(player -> player.getId())
                .toList();
        if (playerIds.isEmpty()) {
            return;
        }
        List<PlayerAnswerJpaEntity> answerEntities =
                GameSessionPersistenceMapper.toAnswerEntities(session, savedEntity);
        if (answerEntities.isEmpty()) {
            answerRepository.deleteBySessionPlayerIdIn(playerIds);
            return;
        }
        List<UUID> answerIds = answerEntities.stream().map(PlayerAnswerJpaEntity::getId).toList();
        answerRepository.deleteBySessionPlayerIdInAndIdNotIn(playerIds, answerIds);
        for (PlayerAnswerJpaEntity answerEntity : answerEntities) {
            answerRepository.getEntityManager().merge(answerEntity);
        }
    }
}
