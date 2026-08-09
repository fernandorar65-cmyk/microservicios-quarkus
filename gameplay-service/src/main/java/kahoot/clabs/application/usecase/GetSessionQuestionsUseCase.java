package kahoot.clabs.application.usecase;

import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import kahoot.clabs.application.dto.QuestionResultResponse;
import kahoot.clabs.application.dto.SessionQuestionResponse;
import kahoot.clabs.application.port.out.read.GameSessionReadPort;
import kahoot.clabs.application.query.GetCurrentSessionQuestionQuery;
import kahoot.clabs.application.query.GetSessionQuestionResultQuery;
import kahoot.clabs.application.query.ListSessionQuestionsQuery;
import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.domain.valueobject.SessionStatus;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class GetSessionQuestionsUseCase {

    @Inject
    GameSessionReadPort gameSessionReadPort;

    public List<SessionQuestionResponse> list(ListSessionQuestionsQuery query) {
        GameSessionReadModel session = requireSession(query.organizationId(), query.sessionId());
        SessionStatus status = SessionStatus.valueOf(session.getStatus());
        boolean reveal = query.asHost() || status == SessionStatus.FINISHED
                || status == SessionStatus.QUESTION_RESULT;
        return session.getQuestions().stream()
                .map(question -> SessionQuestionResponse.from(
                        question, reveal && question.getClosedAt() != null))
                .toList();
    }

    public SessionQuestionResponse current(GetCurrentSessionQuestionQuery query) {
        GameSessionReadModel session = requireSession(query.organizationId(), query.sessionId());
        SessionStatus status = SessionStatus.valueOf(session.getStatus());
        GameSessionReadModel.SessionQuestion question = session.getQuestions().stream()
                .filter(candidate -> candidate.getOrderIndex() == session.getCurrentQuestionIndex())
                .findFirst()
                .orElseThrow(() -> new DomainException("No current question in session"));
        boolean reveal = status == SessionStatus.QUESTION_RESULT || status == SessionStatus.FINISHED;
        return SessionQuestionResponse.from(question, reveal);
    }

    public QuestionResultResponse result(GetSessionQuestionResultQuery query) {
        GameSessionReadModel session = requireSession(query.organizationId(), query.sessionId());
        SessionStatus status = SessionStatus.valueOf(session.getStatus());
        GameSessionReadModel.SessionQuestion question = session.getQuestions().stream()
                .filter(candidate -> candidate.getId().equals(query.sessionQuestionId()))
                .findFirst()
                .orElseThrow(() -> new DomainException(
                        "Session question not found: " + query.sessionQuestionId()));
        boolean closed = question.getClosedAt() != null;
        if (!closed
                && status != SessionStatus.FINISHED
                && status != SessionStatus.QUESTION_RESULT) {
            throw new DomainException("Question results are not available yet");
        }
        if (status == SessionStatus.QUESTION_OPEN
                && question.getOrderIndex() == session.getCurrentQuestionIndex()) {
            throw new DomainException("Question results are not available while the question is open");
        }
        return QuestionResultResponse.from(session, question);
    }

    private GameSessionReadModel requireSession(UUID organizationId, UUID sessionId) {
        return gameSessionReadPort.findById(sessionId)
                .map(session -> {
                    if (!session.getOrganizationId().equals(organizationId)) {
                        throw new DomainException(
                                "Game session does not belong to organization: " + organizationId);
                    }
                    return session;
                })
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
    }
}
