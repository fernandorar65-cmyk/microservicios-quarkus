package kahoot.clabs.infrastructure.persistence.postgres.entity;

import java.time.LocalDateTime;
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
        name = "player_answers",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_player_answers_question_player",
                columnNames = {"session_question_id", "session_player_id"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerAnswerJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_question_id", nullable = false)
    private SessionQuestionJpaEntity sessionQuestion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_player_id", nullable = false)
    private SessionPlayerJpaEntity sessionPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_answer_option_id")
    private SessionAnswerOptionJpaEntity sessionAnswerOption;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "response_time_ms", nullable = false)
    private long responseTimeMs;

    @Column(name = "awarded_points", nullable = false)
    private int awardedPoints;

    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;
}
