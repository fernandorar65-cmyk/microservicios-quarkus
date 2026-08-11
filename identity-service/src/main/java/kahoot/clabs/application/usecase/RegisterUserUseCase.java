package kahoot.clabs.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.command.RegisterUserCommand;
import kahoot.clabs.application.dto.AuthUserResponse;
import kahoot.clabs.application.event.UserIntegrationEvent;
import kahoot.clabs.application.event.UserProjectionSnapshot;
import kahoot.clabs.application.port.integration.UserEventPublisher;
import kahoot.clabs.application.port.write.PasswordHasher;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.exception.EmailAlreadyRegisteredException;
import kahoot.clabs.domain.repository.UserRepository;
import kahoot.clabs.domain.valueobject.Password;

@ApplicationScoped
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final UserEventPublisher userEventPublisher;

    public RegisterUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            UserEventPublisher userEventPublisher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.userEventPublisher = userEventPublisher;
    }

    @Transactional
    public AuthUserResponse execute(RegisterUserCommand command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new EmailAlreadyRegisteredException(command.email());
        }

        Password.assertValidRaw(command.password());
        Password hashedPassword = Password.fromHashed(passwordHasher.hash(command.password()));

        User user = User.create(
                command.email(),
                command.firstName(),
                command.lastName(),
                hashedPassword);

        User saved = userRepository.save(user);
        userEventPublisher.publish(UserIntegrationEvent.userCreated(UserProjectionSnapshot.from(saved, null)));
        return AuthUserResponse.from(saved);
    }
}
