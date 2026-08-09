package kahoot.clabs.quiz.domain.valueobject;

public enum QuizDifficulty {
    EASY("Fácil", "Nivel de dificultad bajo"),
    MODERATE("Moderado", "Nivel de dificultad medio"),
    HARD("Difícil", "Nivel de dificultad alto");

    private final String displayName;
    private final String description;

    QuizDifficulty(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
