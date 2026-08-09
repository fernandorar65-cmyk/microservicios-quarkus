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
        name = "session_players",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_session_players_session_user",
                        columnNames = {"session_id", "user_id"}),
                @UniqueConstraint(
                        name = "uq_session_players_session_nickname",
                        columnNames = {"session_id", "nickname"})
        })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionPlayerJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSessionJpaEntity session;

    /** External reference to identity-service. No FK. */
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "nickname", nullable = false, length = 30)
    private String nickname;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "connected", nullable = false)
    private boolean connected;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;
}
