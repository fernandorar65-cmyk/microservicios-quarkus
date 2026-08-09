package kahoot.clabs.domain.entity;

import java.util.UUID;

import kahoot.clabs.domain.shared.BaseEntity;
import kahoot.clabs.domain.shared.DomainException;

public class SessionAnswerOption extends BaseEntity {

    private UUID sessionQuestionId;
    private final UUID sourceAnswerOptionId;
    private final String text;
    private final boolean correct;
    private final int orderIndex;

    private SessionAnswerOption(
            UUID id,
            UUID sessionQuestionId,
            UUID sourceAnswerOptionId,
            String text,
            boolean correct,
            int orderIndex) {
        super(id);
        if (text == null || text.isBlank()) {
            throw new DomainException("Answer option text is required");
        }
        if (orderIndex < 0) {
            throw new DomainException("Answer option order index cannot be negative");
        }
        this.sessionQuestionId = sessionQuestionId;
        this.sourceAnswerOptionId = sourceAnswerOptionId;
        this.text = text.trim();
        this.correct = correct;
        this.orderIndex = orderIndex;
    }

    public static SessionAnswerOption freeze(
            UUID sessionQuestionId,
            UUID sourceAnswerOptionId,
            String text,
            boolean correct,
            int orderIndex) {
        return new SessionAnswerOption(null, sessionQuestionId, sourceAnswerOptionId, text, correct, orderIndex);
    }

    public static SessionAnswerOption rehydrate(
            UUID id,
            UUID sessionQuestionId,
            UUID sourceAnswerOptionId,
            String text,
            boolean correct,
            int orderIndex) {
        return new SessionAnswerOption(id, sessionQuestionId, sourceAnswerOptionId, text, correct, orderIndex);
    }

    public void assignSessionQuestionId(UUID sessionQuestionId) {
        this.sessionQuestionId = sessionQuestionId;
    }

    public UUID getSessionQuestionId() {
        return sessionQuestionId;
    }

    public UUID getSourceAnswerOptionId() {
        return sourceAnswerOptionId;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public int getOrderIndex() {
        return orderIndex;
    }
}
