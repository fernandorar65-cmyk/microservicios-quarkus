package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.port.out.PlayableQuizSnapshotPort;
import kahoot.clabs.application.port.out.integration.QuizSnapshotPort;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.infrastructure.persistence.mongo.mapper.PlayableQuizSnapshotMapper;
import kahoot.clabs.infrastructure.persistence.mongo.repository.PlayableQuizSnapshotMongoRepository;

@ApplicationScoped
public class PlayableQuizSnapshotMongoAdapter implements QuizSnapshotPort, PlayableQuizSnapshotPort {

    private final PlayableQuizSnapshotMongoRepository repository;

    @Inject
    public PlayableQuizSnapshotMongoAdapter(PlayableQuizSnapshotMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PublishedQuizSnapshot> findPublishedByOrganizationAndId(UUID organizationId, UUID quizId) {
        return repository.findByOrganizationAndQuizId(organizationId, quizId)
                .map(PlayableQuizSnapshotMapper::toSnapshot);
    }

    @Override
    public void upsert(PublishedQuizSnapshot snapshot) {
        repository.persistOrUpdate(PlayableQuizSnapshotMapper.toDocument(snapshot));
    }

    public void upsert(PublishedQuizSnapshot snapshot, String title) {
        repository.persistOrUpdate(PlayableQuizSnapshotMapper.toDocument(snapshot, title));
    }
}
