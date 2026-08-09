package kahoot.clabs.quiz.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.entity.Question;
import kahoot.clabs.quiz.domain.event.QuizPublishedEvent;
import kahoot.clabs.quiz.domain.shared.DomainException;
import kahoot.clabs.quiz.domain.valueobject.MediaType;
import kahoot.clabs.quiz.domain.valueobject.QuestionType;
import kahoot.clabs.quiz.domain.valueobject.QuizDifficulty;

class QuizContentManagementTest {

    @Test
    void managesAnswerOptionsOnlyThroughTheQuizAggregate() {
        Quiz quiz = Quiz.create(UUID.randomUUID(), "Geografía", UUID.randomUUID());
        Question question = quiz.addQuestion("Capital de Colombia", QuestionType.MULTIPLE_CHOICE);
        quiz.addAnswerOption(question.getId(), "Bogotá", true);
        quiz.addAnswerOption(question.getId(), "Medellín", false);

        UUID bogotaId = question.getOptions().getFirst().getId();
        UUID medellinId = question.getOptions().getLast().getId();
        quiz.updateAnswerOption(question.getId(), medellinId, "Cali", false);
        quiz.reorderAnswerOptions(question.getId(), List.of(medellinId, bogotaId));

        assertEquals("Cali", question.getOptions().get(0).getText());
        assertEquals(1, question.getOptions().get(0).getOrderIndex());
        assertEquals("Bogotá", question.getOptions().get(1).getText());
        assertEquals(2, question.getOptions().get(1).getOrderIndex());

        quiz.removeAnswerOption(question.getId(), bogotaId);

        assertEquals(1, question.getOptions().size());
        assertEquals(1, question.getOptions().getFirst().getOrderIndex());
    }

    @Test
    void rejectsAnIncompleteOrRepeatedOptionOrder() {
        Quiz quiz = Quiz.create(UUID.randomUUID(), "Geografía", UUID.randomUUID());
        Question question = quiz.addQuestion("Capital de Colombia", QuestionType.MULTIPLE_CHOICE);
        quiz.addAnswerOption(question.getId(), "Bogotá", true);
        quiz.addAnswerOption(question.getId(), "Cali", false);

        UUID optionId = question.getOptions().getFirst().getId();

        assertThrows(
                DomainException.class,
                () -> quiz.reorderAnswerOptions(question.getId(), List.of(optionId, optionId)));
    }

    @Test
    void updatesAndRemovesTheQuestionAssetThroughTheQuiz() {
        Quiz quiz = Quiz.create(UUID.randomUUID(), "Geografía", UUID.randomUUID());
        Question question = quiz.addQuestion("Capital de Colombia", QuestionType.MULTIPLE_CHOICE);
        quiz.attachAsset(question.getId(), MediaType.IMAGE, "https://example.com/old.webp");
        UUID assetId = question.getAsset().getId();

        quiz.updateQuestionAsset(
                question.getId(),
                assetId,
                MediaType.IMAGE,
                "https://example.com/new.webp",
                "https://example.com/thumb.webp",
                "Mapa de Colombia",
                null);

        assertEquals("https://example.com/new.webp", question.getAsset().getUrl().value());
        assertEquals("Mapa de Colombia", question.getAsset().getAltText());

        quiz.removeQuestionAsset(question.getId(), assetId);

        assertNull(question.getAsset());
    }

    @Test
    void doesNotAllowArchivedQuizzesToBeModified() {
        Quiz quiz = Quiz.create(UUID.randomUUID(), "Geografía", UUID.randomUUID());
        quiz.archive();

        assertThrows(DomainException.class, () -> quiz.changeDifficulty(QuizDifficulty.HARD));
    }

    @Test
    void registersQuizPublishedDomainEventOnPublish() {
        UUID organizationId = UUID.randomUUID();
        UUID createdById = UUID.randomUUID();
        Quiz quiz = Quiz.create(organizationId, "Geografía", createdById);
        Question question = quiz.addQuestion("Capital de Colombia", QuestionType.MULTIPLE_CHOICE);
        quiz.addAnswerOption(question.getId(), "Bogotá", true);
        quiz.addAnswerOption(question.getId(), "Medellín", false);

        quiz.publish();

        assertTrue(quiz.getDomainEvents().stream().anyMatch(QuizPublishedEvent.class::isInstance));
        QuizPublishedEvent event = quiz.getDomainEvents().stream()
                .filter(QuizPublishedEvent.class::isInstance)
                .map(QuizPublishedEvent.class::cast)
                .findFirst()
                .orElseThrow();
        assertEquals(organizationId, event.getOrganizationId());
        assertEquals(createdById, event.getPublishedById());
        assertInstanceOf(QuizPublishedEvent.class, event);
    }
}
