package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.command.ChangePasswordCommand;
import kahoot.clabs.application.port.PasswordHasher;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.exception.InvalidCredentialsException;
import kahoot.clabs.domain.exception.UserNotFoundException;
import kahoot.clabs.domain.repository.UserRepository;
import kahoot.clabs.domain.valueobject.Password;

@ApplicationScoped
public class ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public ChangePasswordUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public void execute(UUID userId, ChangePasswordCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordHasher.matches(command.currentPassword(), user.getPassword().hashedValue())) {
            throw new InvalidCredentialsException();
        }

        Password.assertValidRaw(command.newPassword());
        user.changePassword(Password.fromHashed(passwordHasher.hash(command.newPassword())));
        userRepository.save(user);
    }
}
