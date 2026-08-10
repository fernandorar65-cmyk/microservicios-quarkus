package kahoot.clabs.quiz.application.usecase;

import java.util.Comparator;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.quiz.application.port.read.QuizReadPort;
import kahoot.clabs.quiz.application.query.ListQuizzesQuery;

@ApplicationScoped
public class ListQuizzesUseCase {

    private final QuizReadPort quizReadPort;

    @Inject
    public ListQuizzesUseCase(QuizReadPort quizReadPort) {
        this.quizReadPort = quizReadPort;
    }

    public List<QuizResponse> execute(ListQuizzesQuery query) {
        return quizReadPort.findByOrganization(query.organizationId()).stream()
                .sorted(Comparator.comparing(
                        quiz -> quiz.getUpdatedAt(),
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(QuizResponse::from)
                .toList();
    }
}
