package kahoot.clabs.domain.valueobject;

public enum MemberStatus {
    INVITED("Invitado"),
    ACTIVE("Activo"),
    SUSPENDED("Suspendido");

    private final String description;

    MemberStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
