package kahoot.clabs.application.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import kahoot.clabs.application.readmodel.GameSessionReadModel;

public record QuestionResultResponse(
        UUID sessionQuestionId,
        int orderIndex,
        String title,
        UUID correctOptionId,
        int totalAnswers,
        int correctAnswers,
        Map<UUID, Long> optionCounts,
        List<PlayerAnswerResponse> answers) {

    public static QuestionResultResponse from(
            GameSessionReadModel session, GameSessionReadModel.SessionQuestion question) {
        List<GameSessionReadModel.PlayerAnswer> answers = session.getPlayerAnswers().stream()
                .filter(answer -> question.getId().equals(answer.getSessionQuestionId()))
                .toList();
        Map<UUID, Long> optionCounts = answers.stream()
                .filter(answer -> answer.getSessionAnswerOptionId() != null)
                .collect(Collectors.groupingBy(
                        GameSessionReadModel.PlayerAnswer::getSessionAnswerOptionId,
                        Collectors.counting()));
        UUID correctOptionId = question.getAnswerOptions().stream()
                .filter(GameSessionReadModel.SessionAnswerOption::isCorrect)
                .map(GameSessionReadModel.SessionAnswerOption::getId)
                .findFirst()
                .orElse(null);
        long correctCount = answers.stream().filter(GameSessionReadModel.PlayerAnswer::isCorrect).count();
        return new QuestionResultResponse(
                question.getId(),
                question.getOrderIndex(),
                question.getTitle(),
                correctOptionId,
                answers.size(),
                (int) correctCount,
                optionCounts,
                answers.stream().map(PlayerAnswerResponse::from).toList());
    }
}
