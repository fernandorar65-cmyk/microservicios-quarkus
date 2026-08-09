package kahoot.clabs.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.domain.shared.BaseEntity;
import kahoot.clabs.domain.shared.DomainException;

public class SessionQuestion extends BaseEntity {

    private UUID sessionId;
    private final UUID sourceQuestionId;
    private final int orderIndex;
    private final int points;
    private final int timeLimitSeconds;
    private final String title;
    private final String description;
    private final String questionType;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private final List<SessionAnswerOption> options = new ArrayList<>();

    private SessionQuestion(
            UUID id,
            UUID sessionId,
            UUID sourceQuestionId,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            String title,
            String description,
            String questionType,
            List<SessionAnswerOption> options) {
        super(id);
        if (orderIndex < 0) {
            throw new DomainException("Question order index cannot be negative");
        }
        if (points < 0) {
            throw new DomainException("Points cannot be negative");
        }
        if (timeLimitSeconds <= 0) {
            throw new DomainException("Time limit must be positive");
        }
        this.sessionId = sessionId;
        this.sourceQuestionId = sourceQuestionId;
        this.orderIndex = orderIndex;
        this.points = points;
        this.timeLimitSeconds = timeLimitSeconds;
        this.title = title;
        this.description = description;
        this.questionType = questionType;
        if (options != null) {
            options.forEach(option -> option.assignSessionQuestionId(getId()));
            this.options.addAll(options);
        }
    }

    public static SessionQuestion freeze(
            UUID sessionId,
            UUID sourceQuestionId,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            String title,
            String description,
            String questionType,
            List<SessionAnswerOption> options) {
        return new SessionQuestion(
                null,
                sessionId,
                sourceQuestionId,
                orderIndex,
                points,
                timeLimitSeconds,
                title,
                description,
                questionType,
                options);
    }

    public static SessionQuestion rehydrate(
            UUID id,
            UUID sessionId,
            UUID sourceQuestionId,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            String title,
            String description,
            String questionType,
            LocalDateTime openedAt,
            LocalDateTime closedAt,
            List<SessionAnswerOption> options) {
        SessionQuestion question = new SessionQuestion(
                id,
                sessionId,
                sourceQuestionId,
                orderIndex,
                points,
                timeLimitSeconds,
                title,
                description,
                questionType,
                options);
        question.openedAt = openedAt;
        question.closedAt = closedAt;
        return question;
    }

    public void assignSessionId(UUID sessionId) {
        this.sessionId = sessionId;
        options.forEach(option -> option.assignSessionQuestionId(getId()));
    }

    public void open() {
        if (openedAt != null && closedAt == null) {
            return;
        }
        this.openedAt = LocalDateTime.now();
        this.closedAt = null;
    }

    public void close() {
        if (openedAt == null) {
            throw new DomainException("Cannot close a question that was never opened");
        }
        this.closedAt = LocalDateTime.now();
    }

    public boolean isOpen() {
        return openedAt != null && closedAt == null;
    }

    public boolean isClosed() {
        return closedAt != null;
    }

    public Optional<SessionAnswerOption> findOption(UUID optionId) {
        return options.stream().filter(option -> option.getId().equals(optionId)).findFirst();
    }

    public Optional<SessionAnswerOption> findCorrectOption() {
        return options.stream().filter(SessionAnswerOption::isCorrect).findFirst();
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getSourceQuestionId() {
        return sourceQuestionId;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public int getPoints() {
        return points;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getQuestionType() {
        return questionType;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public List<SessionAnswerOption> getOptions() {
        return Collections.unmodifiableList(options);
    }
}
