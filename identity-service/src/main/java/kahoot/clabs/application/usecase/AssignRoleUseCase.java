package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.command.AssignRoleCommand;
import kahoot.clabs.application.dto.UserProfileResponse;
import kahoot.clabs.application.event.UserIntegrationEvent;
import kahoot.clabs.application.event.UserProjectionSnapshot;
import kahoot.clabs.application.port.integration.UserEventPublisher;
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
    private final UserEventPublisher userEventPublisher;

    public AssignRoleUseCase(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserEventPublisher userEventPublisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userEventPublisher = userEventPublisher;
    }

    @Transactional
    public UserProfileResponse execute(UUID userId, AssignRoleCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Role role = roleRepository.findByType(command.roleType())
                .orElseThrow(() -> new RoleNotFoundException(command.roleType()));

        user.changeRole(role.getId());
        User saved = userRepository.save(user);
        userEventPublisher.publish(UserIntegrationEvent.roleAssigned(UserProjectionSnapshot.from(saved, role)));
        return UserProfileResponse.from(saved);
    }
}
