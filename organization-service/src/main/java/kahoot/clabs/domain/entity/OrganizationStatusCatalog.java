package kahoot.clabs.domain.entity;

import java.util.UUID;

import kahoot.clabs.domain.shared.BaseEntity;
import kahoot.clabs.domain.shared.DomainException;

public class OrganizationStatusCatalog extends BaseEntity {

    private static final int NAME_MAX = 150;
    private static final int DESCRIPTION_MAX = 100;

    private String name;
    private String description;

    private OrganizationStatusCatalog(UUID id, String name, String description) {
        super(id);
        this.name = requireName(name);
        this.description = requireDescription(description);
    }

    public static OrganizationStatusCatalog create(String name, String description) {
        return new OrganizationStatusCatalog(null, name, description);
    }

    public static OrganizationStatusCatalog rehydrate(UUID id, String name, String description) {
        return new OrganizationStatusCatalog(id, name, description);
    }

    public void rename(String name) {
        this.name = requireName(name);
    }

    public void changeDescription(String description) {
        this.description = requireDescription(description);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Organization status name is required");
        }
        String normalized = name.trim();
        if (normalized.length() > NAME_MAX) {
            throw new DomainException("Organization status name must be at most " + NAME_MAX + " characters");
        }
        return normalized;
    }

    private static String requireDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new DomainException("Organization status description is required");
        }
        String normalized = description.trim();
        if (normalized.length() > DESCRIPTION_MAX) {
            throw new DomainException(
                    "Organization status description must be at most " + DESCRIPTION_MAX + " characters");
        }
        return normalized;
    }
}
