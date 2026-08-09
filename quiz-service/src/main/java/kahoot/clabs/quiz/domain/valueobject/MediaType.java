package kahoot.clabs.quiz.domain.valueobject;

public enum MediaType {

    IMAGE("Imagen"),
    VIDEO("Video"),
    AUDIO("Audio"),
    GIF("GIF"),
    DOCUMENT("Documento");

    private final String displayName;

    MediaType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
