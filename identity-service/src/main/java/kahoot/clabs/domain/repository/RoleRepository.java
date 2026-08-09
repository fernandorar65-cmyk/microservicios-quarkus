package kahoot.clabs.domain.repository;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.domain.valueobject.RoleType;
import kahoot.clabs.domain.aggregate.Role;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(UUID id);

    Optional<Role> findByType(RoleType type);

    void delete(Role role);
}
