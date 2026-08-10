package kahoot.clabs.quiz.infrastructure.messaging.kafka;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.entity.AnswerOption;
import kahoot.clabs.quiz.domain.entity.Question;
import kahoot.clabs.quiz.infrastructure.messaging.kafka.QuizPublishedIntegrationEvent.AnswerOptionPayload;
import kahoot.clabs.quiz.infrastructure.messaging.kafka.QuizPublishedIntegrationEvent.Payload;
import kahoot.clabs.quiz.infrastructure.messaging.kafka.QuizPublishedIntegrationEvent.QuestionPayload;

final class QuizPublishedEventMapper {

    private QuizPublishedEventMapper() {
    }

    static QuizPublishedIntegrationEvent toIntegrationEvent(Quiz quiz) {
        return new QuizPublishedIntegrationEvent(
                UUID.randomUUID(),
                QuizPublishedIntegrationEvent.EVENT_TYPE,
                QuizPublishedIntegrationEvent.VERSION,
                Instant.now(),
                quiz.getId(),
                new Payload(
                        quiz.getId(),
                        quiz.getOrganizationId(),
                        quiz.getCreatedById(),
                        quiz.getTitle().value(),
                        mapQuestions(quiz.getQuestions())));
    }

    private static List<QuestionPayload> mapQuestions(List<Question> questions) {
        return questions.stream()
                .sorted(Comparator.comparingInt(Question::getOrderIndex))
                .map(question -> new QuestionPayload(
                        question.getId(),
                        question.getOrderIndex(),
                        question.getPoints().value(),
                        question.getTimeLimit().seconds(),
                        question.getTitle(),
                        question.getDescription(),
                        question.getType().name(),
                        mapOptions(question.getOptions())))
                .toList();
    }

    private static List<AnswerOptionPayload> mapOptions(List<AnswerOption> options) {
        return options.stream()
                .sorted(Comparator.comparingInt(AnswerOption::getOrderIndex))
                .map(option -> new AnswerOptionPayload(
                        option.getId(),
                        option.getText(),
                        option.isCorrect(),
                        option.getOrderIndex()))
                .toList();
    }
}
