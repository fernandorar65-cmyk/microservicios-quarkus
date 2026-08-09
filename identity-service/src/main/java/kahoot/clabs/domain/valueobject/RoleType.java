package kahoot.clabs.domain.valueobject;

public enum RoleType {
    ADMIN("Acceso total a la plataforma"),
    OWNER_ORGANIZATION("Dueño de organización"),
    RH_ORGANIZATION("Recursos humanos de organización"),
    COMMON_MEMBER("Miembro común");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
