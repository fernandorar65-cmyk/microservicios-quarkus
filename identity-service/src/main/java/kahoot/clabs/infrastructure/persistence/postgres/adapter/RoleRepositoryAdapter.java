package kahoot.clabs.infrastructure.persistence.postgres.adapter;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.repository.RoleRepository;
import kahoot.clabs.domain.valueobject.RoleType;
import kahoot.clabs.infrastructure.persistence.postgres.entity.RoleJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.mapper.RolePersistenceMapper;
import kahoot.clabs.infrastructure.persistence.postgres.repository.RolePanacheRepository;

@ApplicationScoped
public class RoleRepositoryAdapter implements RoleRepository {

    private final RolePanacheRepository roleRepository;

    public RoleRepositoryAdapter(RolePanacheRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public Role save(Role role) {
        RoleJpaEntity entity = RolePersistenceMapper.toEntity(role);
        if (roleRepository.findByIdOptional(entity.getId()).isEmpty()) {
            roleRepository.persist(entity);
        } else {
            entity = roleRepository.getEntityManager().merge(entity);
        }
        return RolePersistenceMapper.toDomain(entity);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return roleRepository.findByIdOptional(id).map(RolePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Role> findByType(RoleType type) {
        return roleRepository.findByType(type.name()).map(RolePersistenceMapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(Role role) {
        roleRepository.deleteById(role.getId());
    }
}
