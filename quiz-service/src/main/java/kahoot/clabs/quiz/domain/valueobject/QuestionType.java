package kahoot.clabs.quiz.domain.valueobject;

public enum QuestionType {

    MULTIPLE_CHOICE("Selección Múltiple", "El usuario elige una sola respuesta correcta"),
    TRUE_FALSE("Verdadero / Falso", "Pregunta binaria"),
    MULTIPLE_SELECT("Selección Múltiple Avanzada", "El usuario puede seleccionar varias respuestas"),
    SHORT_ANSWER("Respuesta Corta", "El usuario escribe una respuesta libre");

    private final String displayName;
    private final String description;

    QuestionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
