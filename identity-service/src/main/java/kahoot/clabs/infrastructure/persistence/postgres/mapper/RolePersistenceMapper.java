package kahoot.clabs.infrastructure.persistence.postgres.mapper;

import java.util.List;
import java.util.stream.Collectors;

import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.entity.Permission;
import kahoot.clabs.domain.valueobject.RoleType;
import kahoot.clabs.infrastructure.persistence.postgres.entity.RoleJpaEntity;

public final class RolePersistenceMapper {

    private RolePersistenceMapper() {
    }

    public static RoleJpaEntity toEntity(Role role) {
        RoleJpaEntity entity = new RoleJpaEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        entity.setType(role.getType().name());
        entity.setDescription(role.getDescription());
        entity.setCreatedAt(role.getCreatedAt());
        entity.setUpdatedAt(role.getUpdatedAt());
        entity.setPermissions(role.getPermissions().stream()
                .map(PermissionPersistenceMapper::toEntity)
                .collect(Collectors.toSet()));
        return entity;
    }

    public static Role toDomain(RoleJpaEntity entity) {
        List<Permission> permissions = entity.getPermissions().stream()
                .map(PermissionPersistenceMapper::toDomain)
                .toList();
        return Role.rehydrate(
                entity.getId(),
                entity.getName(),
                RoleType.valueOf(entity.getType()),
                entity.getDescription(),
                permissions,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
