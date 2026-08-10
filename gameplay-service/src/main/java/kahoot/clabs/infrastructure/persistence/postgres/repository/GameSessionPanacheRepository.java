package kahoot.clabs.infrastructure.persistence.postgres.repository;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.Hibernate;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.postgres.entity.GameSessionJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.SessionQuestionJpaEntity;


@ApplicationScoped
public class GameSessionPanacheRepository implements PanacheRepositoryBase<GameSessionJpaEntity, UUID> {

    public boolean existsById(UUID id) {
        return count("id = ?1", id) > 0;
    }

    public Optional<GameSessionJpaEntity> findByIdWithDetails(UUID id) {
        Optional<GameSessionJpaEntity> found = findByIdOptional(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }

        GameSessionJpaEntity session = found.get();
        Hibernate.initialize(session.getPlayers());
        Hibernate.initialize(session.getQuestions());
        for (SessionQuestionJpaEntity question : session.getQuestions()) {
            Hibernate.initialize(question.getAnswerOptions());
        }
        return Optional.of(session);
    }
}
