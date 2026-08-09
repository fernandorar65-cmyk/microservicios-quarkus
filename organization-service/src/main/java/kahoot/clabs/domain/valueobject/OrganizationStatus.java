package kahoot.clabs.domain.valueobject;

public enum OrganizationStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),
    SUSPENDED("Suspendido"),
    PENDING("Pendiente de activación");

    private final String description;

    OrganizationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
