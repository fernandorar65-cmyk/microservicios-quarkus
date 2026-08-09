package kahoot.clabs.quiz.domain.entity;

import java.util.UUID;

import kahoot.clabs.quiz.domain.shared.BaseEntity;
import kahoot.clabs.quiz.domain.shared.DomainException;

public class AnswerOption extends BaseEntity {

    private UUID questionId;
    private String text;
    private boolean correct;
    private int orderIndex;
    private String explanation;

    private AnswerOption(String text, boolean correct) {
        this(null, text, correct);
    }

    private AnswerOption(UUID id, String text, boolean correct) {
        super(id);
        validateText(text);
        this.text = text.trim();
        this.correct = correct;
    }

    public static AnswerOption create(String text, boolean correct) {
        return new AnswerOption(text, correct);
    }

    public static AnswerOption rehydrate(
            UUID id,
            UUID questionId,
            String text,
            boolean correct,
            int orderIndex,
            String explanation) {
        AnswerOption option = new AnswerOption(id, text, correct);
        option.questionId = questionId;
        option.assignOrderIndex(orderIndex);
        option.explanation = explanation;
        return option;
    }

    private void validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new DomainException("Answer option text cannot be empty");
        }
        if (text.trim().length() > 500) {
            throw new DomainException("Answer option text cannot exceed 500 characters");
        }
    }

    void assignQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    void assignOrderIndex(int orderIndex) {
        if (orderIndex < 1) {
            throw new DomainException("Answer option order index must be at least 1");
        }
        this.orderIndex = orderIndex;
    }

    void updateText(String newText) {
        validateText(newText);
        this.text = newText.trim();
    }

    void markAsCorrect(boolean correct) {
        this.correct = correct;
    }

    public void changeExplanation(String explanation) {
        this.explanation = explanation;
    }

    public UUID getQuestionId() {
        return questionId;
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

    public String getExplanation() {
        return explanation;
    }
}
