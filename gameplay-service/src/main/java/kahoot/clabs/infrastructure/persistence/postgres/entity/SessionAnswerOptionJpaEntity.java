package kahoot.clabs.infrastructure.persistence.postgres.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "session_answer_options",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_session_answer_options_question_order",
                columnNames = {"session_question_id", "order_index"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionAnswerOptionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_question_id", nullable = false)
    private SessionQuestionJpaEntity sessionQuestion;

    /** Conceptual reference to quiz-service answer option. No FK. */
    @Column(name = "source_answer_option_id")
    private UUID sourceAnswerOptionId;

    @Column(name = "text", nullable = false, length = 500)
    private String text;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;
}
