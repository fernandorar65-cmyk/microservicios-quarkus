package kahoot.clabs.infrastructure.persistence.postgres.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.domain.entity.Permission;
import kahoot.clabs.domain.repository.PermissionRepository;
import kahoot.clabs.infrastructure.persistence.postgres.entity.PermissionJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.mapper.PermissionPersistenceMapper;
import kahoot.clabs.infrastructure.persistence.postgres.repository.PermissionPanacheRepository;

@ApplicationScoped
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final PermissionPanacheRepository permissionRepository;

    public PermissionRepositoryAdapter(PermissionPanacheRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public Permission save(Permission permission) {
        PermissionJpaEntity entity = PermissionPersistenceMapper.toEntity(permission);
        if (permissionRepository.findByIdOptional(entity.getId()).isEmpty()) {
            permissionRepository.persist(entity);
        } else {
            entity = permissionRepository.getEntityManager().merge(entity);
        }
        return PermissionPersistenceMapper.toDomain(entity);
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return permissionRepository.findByIdOptional(id).map(PermissionPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByNameAndModule(String name, String module) {
        return permissionRepository.findByNameIgnoreCaseAndModuleIgnoreCase(name, module)
                .map(PermissionPersistenceMapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.listAll().stream()
                .map(PermissionPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findAllByRoleId(UUID roleId) {
        return permissionRepository.findByRoleId(roleId).stream()
                .map(PermissionPersistenceMapper::toDomain)
                .toList();
    }
}
