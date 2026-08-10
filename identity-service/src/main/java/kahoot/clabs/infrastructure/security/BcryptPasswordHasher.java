package kahoot.clabs.infrastructure.security;

import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.application.port.write.PasswordHasher;

import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class BcryptPasswordHasher implements PasswordHasher {

    @Override
    public String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
