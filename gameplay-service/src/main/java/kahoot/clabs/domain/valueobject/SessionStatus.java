package kahoot.clabs.domain.valueobject;

public enum SessionStatus {
    LOBBY,
    QUESTION_OPEN,
    QUESTION_RESULT,
    FINISHED,
    CANCELLED;

    public boolean isTerminal() {
        return this == FINISHED || this == CANCELLED;
    }

    public boolean allowsJoin() {
        return this == LOBBY;
    }

    public boolean allowsAnswers() {
        return this == QUESTION_OPEN;
    }
}
