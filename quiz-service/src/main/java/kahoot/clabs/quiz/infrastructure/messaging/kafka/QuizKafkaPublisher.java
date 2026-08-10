package kahoot.clabs.quiz.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.port.write.QuizPublishedIntegrationPort;
import kahoot.clabs.quiz.domain.aggregate.Quiz;

@ApplicationScoped
public class QuizKafkaPublisher implements QuizPublishedIntegrationPort {

    private static final Logger LOG = Logger.getLogger(QuizKafkaPublisher.class);

    private final Emitter<Record<String, QuizPublishedIntegrationEvent>> emitter;

    @Inject
    public QuizKafkaPublisher(
            @Channel("quiz-events") Emitter<Record<String, QuizPublishedIntegrationEvent>> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void publish(Quiz quiz) {
        QuizPublishedIntegrationEvent event = QuizPublishedEventMapper.toIntegrationEvent(quiz);
        LOG.infof(
                "Publishing QuizPublished to quiz.events quizId=%s organizationId=%s eventId=%s",
                event.payload().quizId(),
                event.payload().organizationId(),
                event.eventId());
        emitter.send(Record.of(event.aggregateId().toString(), event));
    }
}
