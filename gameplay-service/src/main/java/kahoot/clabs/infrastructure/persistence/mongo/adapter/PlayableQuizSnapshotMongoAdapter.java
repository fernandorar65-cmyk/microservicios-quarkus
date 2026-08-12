package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import kahoot.clabs.application.port.integration.QuizSnapshotPort;
import kahoot.clabs.application.port.write.PlayableQuizSnapshotPort;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.infrastructure.persistence.mongo.mapper.PlayableQuizSnapshotMapper;
import kahoot.clabs.infrastructure.persistence.mongo.repository.PlayableQuizSnapshotMongoRepository;

/**
 * Mongo playable-quiz read/write adapter.
 * {@link TxType#NOT_SUPPORTED} suspends any active JPA/JTA TX — standalone Mongo rejects txn numbers.
 */
@ApplicationScoped
public class PlayableQuizSnapshotMongoAdapter implements QuizSnapshotPort, PlayableQuizSnapshotPort {

    private final PlayableQuizSnapshotMongoRepository repository;

    @Inject
    public PlayableQuizSnapshotMongoAdapter(PlayableQuizSnapshotMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(TxType.NOT_SUPPORTED)
    public Optional<PublishedQuizSnapshot> findPublishedByOrganizationAndId(UUID organizationId, UUID quizId) {
        return repository.findByOrganizationAndQuizId(organizationId, quizId)
                .map(PlayableQuizSnapshotMapper::toSnapshot);
    }

    @Override
    @Transactional(TxType.NOT_SUPPORTED)
    public void upsert(PublishedQuizSnapshot snapshot) {
        repository.persistOrUpdate(PlayableQuizSnapshotMapper.toDocument(snapshot));
    }

    @Transactional(TxType.NOT_SUPPORTED)
    public void upsert(PublishedQuizSnapshot snapshot, String title) {
        repository.persistOrUpdate(PlayableQuizSnapshotMapper.toDocument(snapshot, title));
    }
}
