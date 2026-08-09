package kahoot.clabs.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.command.LoginCommand;
import kahoot.clabs.application.dto.AuthUserResponse;
import kahoot.clabs.application.port.PasswordHasher;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.exception.InvalidCredentialsException;
import kahoot.clabs.domain.repository.UserRepository;

@ApplicationScoped
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public LoginUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public AuthUserResponse execute(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(command.password(), user.getPassword().hashedValue())) {
            throw new InvalidCredentialsException();
        }

        user.recordLogin();
        return AuthUserResponse.from(userRepository.save(user));
    }
}
