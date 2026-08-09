package kahoot.clabs.infrastructure.persistence.postgres.mapper;

import kahoot.clabs.domain.entity.Permission;
import kahoot.clabs.infrastructure.persistence.postgres.entity.PermissionJpaEntity;

public final class PermissionPersistenceMapper {

    private PermissionPersistenceMapper() {
    }

    public static PermissionJpaEntity toEntity(Permission permission) {
        PermissionJpaEntity entity = new PermissionJpaEntity();
        entity.setId(permission.getId());
        entity.setName(permission.getName());
        entity.setDescription(permission.getDescription());
        entity.setModule(permission.getModule());
        entity.setCreatedAt(permission.getCreatedAt());
        entity.setUpdatedAt(permission.getUpdatedAt());
        return entity;
    }

    public static Permission toDomain(PermissionJpaEntity entity) {
        return Permission.rehydrate(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getModule(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
