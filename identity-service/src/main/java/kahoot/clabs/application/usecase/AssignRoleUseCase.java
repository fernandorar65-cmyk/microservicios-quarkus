package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.command.AssignRoleCommand;
import kahoot.clabs.application.dto.UserProfileResponse;
import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.exception.RoleNotFoundException;
import kahoot.clabs.domain.exception.UserNotFoundException;
import kahoot.clabs.domain.repository.RoleRepository;
import kahoot.clabs.domain.repository.UserRepository;

@ApplicationScoped
public class AssignRoleUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AssignRoleUseCase(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public UserProfileResponse execute(UUID userId, AssignRoleCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Role role = roleRepository.findByType(command.roleType())
                .orElseThrow(() -> new RoleNotFoundException(command.roleType()));

        user.changeRole(role.getId());
        return UserProfileResponse.from(userRepository.save(user));
    }
}
