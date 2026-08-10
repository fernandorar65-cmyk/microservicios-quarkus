package kahoot.clabs.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.command.LoginCommand;
import kahoot.clabs.application.dto.AuthUserResponse;
import kahoot.clabs.application.port.write.PasswordHasher;
import kahoot.clabs.application.port.write.UserProjectionPort;
import kahoot.clabs.application.readmodel.UserReadModels;
import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.exception.InvalidCredentialsException;
import kahoot.clabs.domain.repository.RoleRepository;
import kahoot.clabs.domain.repository.UserRepository;

@ApplicationScoped
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;
    private final UserProjectionPort userProjectionPort;

    public LoginUserUseCase(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher,
            UserProjectionPort userProjectionPort) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
        this.userProjectionPort = userProjectionPort;
    }

    @Transactional
    public AuthUserResponse execute(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(command.password(), user.getPassword().hashedValue())) {
            throw new InvalidCredentialsException();
        }

        user.recordLogin();
        User saved = userRepository.save(user);
        userProjectionPort.save(UserReadModels.from(saved, resolveRole(saved)));
        return AuthUserResponse.from(saved);
    }

    private Role resolveRole(User user) {
        if (user.getRoleId() == null) {
            return null;
        }
        return roleRepository.findById(user.getRoleId()).orElse(null);
    }
}
