package kahoot.clabs.quiz.domain.entity;

import java.util.UUID;

import kahoot.clabs.quiz.domain.shared.BaseEntity;
import kahoot.clabs.quiz.domain.shared.DomainException;

public class Category extends BaseEntity {

    private final UUID organizationId;
    private String name;
    private String description;
    private String color;
    private String icon;

    private Category(UUID id, UUID organizationId, String name, String description, String color) {
        super(id);
        if (organizationId == null) {
            throw new DomainException("Organization id is required");
        }
        if (name == null || name.isBlank()) {
            throw new DomainException("Category name is required");
        }
        this.organizationId = organizationId;
        this.name = name.trim();
        this.description = description;
        this.color = color;
    }

    public static Category create(UUID organizationId, String name) {
        return new Category(null, organizationId, name, null, null);
    }

    public static Category rehydrate(UUID id, UUID organizationId, String name, String description, String color) {
        return new Category(id, organizationId, name, description, color);
    }

    public static Category rehydrate(
            UUID id,
            UUID organizationId,
            String name,
            String description,
            String color,
            String icon) {
        Category category = new Category(id, organizationId, name, description, color);
        category.icon = icon;
        return category;
    }

    public void rename(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Category name is required");
        }
        this.name = name.trim();
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeColor(String color) {
        this.color = color;
    }

    public void changeIcon(String icon) {
        this.icon = icon;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }
}
