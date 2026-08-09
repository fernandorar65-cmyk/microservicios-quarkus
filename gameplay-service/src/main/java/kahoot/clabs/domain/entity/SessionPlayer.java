package kahoot.clabs.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.domain.valueobject.Nickname;
import kahoot.clabs.domain.shared.BaseEntity;
import kahoot.clabs.domain.shared.DomainException;

public class SessionPlayer extends BaseEntity {

    private UUID sessionId;
    private final UUID userId;
    private Nickname nickname;
    private int score;
    private boolean connected;
    private final LocalDateTime joinedAt;
    private LocalDateTime leftAt;

    private SessionPlayer(
            UUID id,
            UUID sessionId,
            UUID userId,
            Nickname nickname,
            int score,
            boolean connected,
            LocalDateTime joinedAt,
            LocalDateTime leftAt) {
        super(id);
        if (userId == null) {
            throw new DomainException("Player user id is required");
        }
        if (nickname == null) {
            throw new DomainException("Nickname is required");
        }
        this.sessionId = sessionId;
        this.userId = userId;
        this.nickname = nickname;
        this.score = score;
        this.connected = connected;
        this.joinedAt = joinedAt != null ? joinedAt : LocalDateTime.now();
        this.leftAt = leftAt;
    }

    public static SessionPlayer join(UUID sessionId, UUID userId, Nickname nickname) {
        return new SessionPlayer(null, sessionId, userId, nickname, 0, true, LocalDateTime.now(), null);
    }

    public static SessionPlayer rehydrate(
            UUID id,
            UUID sessionId,
            UUID userId,
            String nickname,
            int score,
            boolean connected,
            LocalDateTime joinedAt,
            LocalDateTime leftAt) {
        return new SessionPlayer(
                id, sessionId, userId, Nickname.of(nickname), score, connected, joinedAt, leftAt);
    }

    public void assignSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public void changeNickname(Nickname nickname) {
        if (nickname == null) {
            throw new DomainException("Nickname is required");
        }
        this.nickname = nickname;
    }

    public void leave() {
        this.connected = false;
        this.leftAt = LocalDateTime.now();
    }

    public void reconnect() {
        this.connected = true;
        this.leftAt = null;
    }

    public void addScore(int points) {
        if (points < 0) {
            throw new DomainException("Awarded points cannot be negative");
        }
        this.score += points;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public Nickname getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }

    public boolean isConnected() {
        return connected;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }
}
