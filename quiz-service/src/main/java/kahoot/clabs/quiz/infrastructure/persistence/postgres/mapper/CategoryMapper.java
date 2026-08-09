package kahoot.clabs.quiz.infrastructure.persistence.postgres.mapper;

import java.time.LocalDateTime;

import kahoot.clabs.quiz.domain.entity.Category;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.entity.CategoryJpaEntity;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryJpaEntity toEntity(Category category) {
        CategoryJpaEntity entity = CategoryJpaEntity.create();
        entity.setId(category.getId());
        entity.setOrganizationId(category.getOrganizationId());
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());
        entity.setColor(category.getColor());
        entity.setIcon(category.getIcon());
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }

    public static Category toDomain(CategoryJpaEntity entity) {
        return Category.rehydrate(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getName(),
                entity.getDescription(),
                entity.getColor(),
                entity.getIcon());
    }
}
