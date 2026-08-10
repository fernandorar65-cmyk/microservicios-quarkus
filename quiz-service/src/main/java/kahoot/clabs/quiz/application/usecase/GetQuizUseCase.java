package kahoot.clabs.quiz.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.quiz.application.port.read.QuizReadPort;
import kahoot.clabs.quiz.application.query.GetQuizQuery;
import kahoot.clabs.quiz.application.readmodel.QuizReadModel;
import kahoot.clabs.quiz.domain.shared.DomainException;

@ApplicationScoped
public class GetQuizUseCase {

    private final QuizReadPort quizReadPort;

    @Inject
    public GetQuizUseCase(QuizReadPort quizReadPort) {
        this.quizReadPort = quizReadPort;
    }

    public QuizResponse execute(GetQuizQuery query) {
        QuizReadModel quiz = quizReadPort.findById(query.quizId())
                .orElseThrow(() -> new DomainException("Quiz not found: " + query.quizId()));
        if (!quiz.getOrganizationId().equals(query.organizationId())) {
            throw new DomainException("Quiz does not belong to organization: " + query.organizationId());
        }
        return QuizResponse.fromDetails(quiz);
    }
}
