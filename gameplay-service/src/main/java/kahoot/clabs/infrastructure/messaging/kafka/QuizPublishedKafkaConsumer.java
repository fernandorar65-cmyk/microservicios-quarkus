package kahoot.clabs.infrastructure.messaging.kafka;

import java.util.Comparator;
import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot.AnswerOptionSnapshot;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot.QuestionSnapshot;
import kahoot.clabs.infrastructure.messaging.kafka.QuizPublishedIntegrationEvent.AnswerOptionPayload;
import kahoot.clabs.infrastructure.messaging.kafka.QuizPublishedIntegrationEvent.QuestionPayload;
import kahoot.clabs.infrastructure.persistence.mongo.adapter.PlayableQuizSnapshotMongoAdapter;

/**
 * Idempotent projection: QuizPublished → local playable quiz snapshot (Mongo).
 */
@ApplicationScoped
public class QuizPublishedKafkaConsumer {

    private static final Logger LOG = Logger.getLogger(QuizPublishedKafkaConsumer.class);

    private final PlayableQuizSnapshotMongoAdapter snapshotAdapter;

    @Inject
    public QuizPublishedKafkaConsumer(PlayableQuizSnapshotMongoAdapter snapshotAdapter) {
        this.snapshotAdapter = snapshotAdapter;
    }

    @Incoming("quiz-published-in")
    public void onQuizPublished(QuizPublishedIntegrationEvent event) {
        if (event == null || event.payload() == null) {
            LOG.warn("Ignoring empty QuizPublished message");
            return;
        }
        if (!QuizPublishedIntegrationEvent.EVENT_TYPE.equals(event.eventType())) {
            LOG.debugf("Ignoring quiz.events message eventType=%s", event.eventType());
            return;
        }

        PublishedQuizSnapshot snapshot = toSnapshot(event);
        snapshotAdapter.upsert(snapshot, event.payload().title());
        LOG.infof(
                "Projected QuizPublished snapshot quizId=%s organizationId=%s eventId=%s",
                snapshot.quizId(),
                snapshot.organizationId(),
                event.eventId());
    }

    private static PublishedQuizSnapshot toSnapshot(QuizPublishedIntegrationEvent event) {
        List<QuestionSnapshot> questions = event.payload().questions() == null
                ? List.of()
                : event.payload().questions().stream()
                        .sorted(Comparator.comparingInt(QuestionPayload::orderIndex))
                        .map(QuizPublishedKafkaConsumer::toQuestion)
                        .toList();
        return new PublishedQuizSnapshot(
                event.payload().quizId(),
                event.payload().organizationId(),
                questions);
    }

    private static QuestionSnapshot toQuestion(QuestionPayload question) {
        List<AnswerOptionSnapshot> options = question.options() == null
                ? List.of()
                : question.options().stream()
                        .sorted(Comparator.comparingInt(AnswerOptionPayload::orderIndex))
                        .map(option -> new AnswerOptionSnapshot(
                                option.id(),
                                option.text(),
                                option.correct(),
                                option.orderIndex()))
                        .toList();
        return new QuestionSnapshot(
                question.id(),
                question.orderIndex(),
                question.points(),
                question.timeLimitSeconds(),
                question.title(),
                question.description(),
                question.type(),
                options);
    }
}
