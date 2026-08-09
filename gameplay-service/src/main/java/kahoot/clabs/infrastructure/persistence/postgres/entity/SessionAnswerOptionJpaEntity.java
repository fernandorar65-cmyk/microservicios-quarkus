package kahoot.clabs.gameplay.infrastructure.persistence.postgres.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "session_answer_options",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_session_answer_options_question_position",
                columnNames = {"session_question_id", "position"}))
public class SessionAnswerOptionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_question_id", nullable = false)
    private SessionQuestionJpaEntity sessionQuestion;

    /** Conceptual reference to quiz-service answer option. No FK. */
    @Column(name = "source_answer_option_id", nullable = false, updatable = false)
    private UUID sourceAnswerOptionId;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "position", nullable = false)
    private int position;

    protected SessionAnswerOptionJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SessionQuestionJpaEntity getSessionQuestion() {
        return sessionQuestion;
    }

    public void setSessionQuestion(SessionQuestionJpaEntity sessionQuestion) {
        this.sessionQuestion = sessionQuestion;
    }

    public UUID getSourceAnswerOptionId() {
        return sourceAnswerOptionId;
    }

    public void setSourceAnswerOptionId(UUID sourceAnswerOptionId) {
        this.sourceAnswerOptionId = sourceAnswerOptionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
