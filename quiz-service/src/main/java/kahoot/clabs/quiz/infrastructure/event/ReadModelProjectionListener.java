package kahoot.clabs.quiz.infrastructure.event;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.application.event.CategoryReadModelDeletedEvent;
import kahoot.clabs.quiz.application.event.CategoryReadModelUpsertedEvent;
import kahoot.clabs.quiz.application.event.QuizReadModelDeletedEvent;
import kahoot.clabs.quiz.application.event.QuizReadModelUpsertedEvent;
import kahoot.clabs.quiz.application.port.write.CategoryProjectionPort;
import kahoot.clabs.quiz.application.port.write.QuizProjectionPort;

@ApplicationScoped
public class ReadModelProjectionListener {

    private static final Logger log = Logger.getLogger(ReadModelProjectionListener.class);

    private final QuizProjectionPort quizProjectionPort;
    private final CategoryProjectionPort categoryProjectionPort;

    @Inject
    public ReadModelProjectionListener(
            QuizProjectionPort quizProjectionPort, CategoryProjectionPort categoryProjectionPort) {
        this.quizProjectionPort = quizProjectionPort;
        this.categoryProjectionPort = categoryProjectionPort;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    void onQuizUpserted(@Observes(during = TransactionPhase.AFTER_SUCCESS) QuizReadModelUpsertedEvent event) {
        log.infof("projecting quiz upsert aggregateId=%s", event.readModel().getId());
        quizProjectionPort.save(event.readModel());
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    void onQuizDeleted(@Observes(during = TransactionPhase.AFTER_SUCCESS) QuizReadModelDeletedEvent event) {
        log.infof("projecting quiz delete aggregateId=%s", event.quizId());
        quizProjectionPort.deleteById(event.quizId());
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    void onCategoryUpserted(@Observes(during = TransactionPhase.AFTER_SUCCESS) CategoryReadModelUpsertedEvent event) {
        log.infof("projecting category upsert aggregateId=%s", event.readModel().getId());
        categoryProjectionPort.save(event.readModel());
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    void onCategoryDeleted(@Observes(during = TransactionPhase.AFTER_SUCCESS) CategoryReadModelDeletedEvent event) {
        log.infof("projecting category delete aggregateId=%s", event.categoryId());
        categoryProjectionPort.deleteById(event.categoryId());
    }
}
