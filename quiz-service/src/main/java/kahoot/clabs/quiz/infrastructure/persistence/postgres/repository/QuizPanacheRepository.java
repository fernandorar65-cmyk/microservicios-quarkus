package kahoot.clabs.quiz.infrastructure.persistence.postgres.repository;

import java.util.Optional;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.QuestionJpaEntity;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.QuizJpaEntity;

/**
 * Write-side only. List/get quiz queries belong on Mongo {@code QuizReadPort}.
 * {@link #findByIdWithDetails} exists solely to rehydrate the aggregate for commands.
 */
@ApplicationScoped
public class QuizPanacheRepository implements PanacheRepositoryBase<QuizJpaEntity, UUID> {

    /**
     * Split fetches avoid Hibernate {@code MultipleBagFetchException}.
     * Cannot {@code join fetch} two bags in one query ({@code Quiz.questions} + {@code Question.answerOptions}).
     */
    public Optional<QuizJpaEntity> findByIdWithDetails(UUID id) {
        Optional<QuizJpaEntity> quiz = find("""
                select distinct q
                from QuizJpaEntity q
                left join fetch q.categories
                left join fetch q.questions questions
                left join fetch questions.asset
                where q.id = ?1
                """, id).firstResultOptional();
        if (quiz.isEmpty()) {
            return Optional.empty();
        }

        // Hydrate answerOptions on the same persistence-context Question instances
        getEntityManager().createQuery("""
                select distinct qu
                from QuestionJpaEntity qu
                left join fetch qu.answerOptions
                where qu.quiz.id = :quizId
                """, QuestionJpaEntity.class)
                .setParameter("quizId", id)
                .getResultList();

        return quiz;
    }

    public boolean existsByOrganizationIdAndTitleIgnoreCase(UUID organizationId, String title) {
        return count("organizationId = ?1 and lower(title) = lower(?2)", organizationId, title) > 0;
    }
}
