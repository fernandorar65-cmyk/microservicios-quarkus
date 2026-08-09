package kahoot.clabs.quiz.domain.valueobject;

public enum QuizStatus {
    DRAFT("Borrador"),
    PUBLISHED("Publicado"),
    ARCHIVED("Archivado");

    private final String description;

    QuizStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
