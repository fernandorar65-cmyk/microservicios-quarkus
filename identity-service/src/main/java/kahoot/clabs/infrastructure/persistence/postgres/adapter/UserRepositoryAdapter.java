package kahoot.clabs.infrastructure.persistence.postgres.adapter;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.repository.UserRepository;
import kahoot.clabs.infrastructure.persistence.postgres.entity.RoleJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.UserJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.mapper.UserPersistenceMapper;
import kahoot.clabs.infrastructure.persistence.postgres.repository.UserPanacheRepository;

@ApplicationScoped
public class UserRepositoryAdapter implements UserRepository {

    private final UserPanacheRepository userRepository;

    public UserRepositoryAdapter(UserPanacheRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity entity = UserPersistenceMapper.toEntity(user);
        if (user.getRoleId() != null) {
            RoleJpaEntity roleRef = userRepository.getEntityManager()
                    .getReference(RoleJpaEntity.class, user.getRoleId());
            entity.setRole(roleRef);
        } else {
            entity.setRole(null);
        }

        if (userRepository.findByIdOptional(entity.getId()).isEmpty()) {
            userRepository.persist(entity);
        } else {
            entity = userRepository.getEntityManager().merge(entity);
        }

        UUID roleId = entity.getRole() != null ? entity.getRole().getId() : null;
        return UserPersistenceMapper.toDomain(entity, roleId);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findByIdOptional(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).map(this::toDomain);
    }

    @Override
    @Transactional
    public void delete(User user) {
        userRepository.deleteById(user.getId());
    }

    private User toDomain(UserJpaEntity entity) {
        UUID roleId = entity.getRole() != null ? entity.getRole().getId() : null;
        return UserPersistenceMapper.toDomain(entity, roleId);
    }
}
