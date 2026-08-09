package kahoot.clabs.quiz.infrastructure.persistence.postgres.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.event.QuizReadModelDeletedEvent;
import kahoot.clabs.quiz.application.event.QuizReadModelUpsertedEvent;
import kahoot.clabs.quiz.application.readmodel.QuizReadModels;
import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.quiz.domain.shared.DomainEvent;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.mapper.QuizMapper;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.repository.QuizPanacheRepository;

@ApplicationScoped
public class JpaQuizRepositoryAdapter implements QuizRepository {

    private final QuizPanacheRepository quizPanacheRepository;
    private final Event<QuizReadModelUpsertedEvent> quizReadModelUpsertedEvent;
    private final Event<QuizReadModelDeletedEvent> quizReadModelDeletedEvent;
    private final Event<DomainEvent> domainEvent;

    @Inject
    public JpaQuizRepositoryAdapter(
            QuizPanacheRepository quizPanacheRepository,
            Event<QuizReadModelUpsertedEvent> quizReadModelUpsertedEvent,
            Event<QuizReadModelDeletedEvent> quizReadModelDeletedEvent,
            Event<DomainEvent> domainEvent) {
        this.quizPanacheRepository = quizPanacheRepository;
        this.quizReadModelUpsertedEvent = quizReadModelUpsertedEvent;
        this.quizReadModelDeletedEvent = quizReadModelDeletedEvent;
        this.domainEvent = domainEvent;
    }

    @Override
    @Transactional
    public Quiz save(Quiz quiz) {
        List<DomainEvent> domainEvents = quiz.pullDomainEvents();
        Quiz saved = QuizMapper.toDomain(quizPanacheRepository.getEntityManager().merge(QuizMapper.toEntity(quiz)));
        quizReadModelUpsertedEvent.fire(new QuizReadModelUpsertedEvent(QuizReadModels.from(saved)));
        domainEvents.forEach(domainEvent::fire);
        return saved;
    }

    @Override
    public Optional<Quiz> findById(UUID id) {
        return quizPanacheRepository.findByIdWithDetails(id).map(QuizMapper::toDomain);
    }

    @Override
    public boolean existsByOrganizationIdAndTitleIgnoreCase(UUID organizationId, String title) {
        return quizPanacheRepository.existsByOrganizationIdAndTitleIgnoreCase(organizationId, title);
    }

    @Override
    public boolean existsById(UUID id) {
        return quizPanacheRepository.findByIdOptional(id).isPresent();
    }

    @Override
    @Transactional
    public void delete(Quiz quiz) {
        quizPanacheRepository.delete(QuizMapper.toEntity(quiz));
        quizReadModelDeletedEvent.fire(new QuizReadModelDeletedEvent(quiz.getId()));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        quizPanacheRepository.deleteById(id);
        quizReadModelDeletedEvent.fire(new QuizReadModelDeletedEvent(id));
    }
}
