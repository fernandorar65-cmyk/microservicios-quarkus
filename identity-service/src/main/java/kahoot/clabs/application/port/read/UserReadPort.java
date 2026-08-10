package kahoot.clabs.application.port.read;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.application.readmodel.UserReadModel;

public interface UserReadPort {

    Optional<UserReadModel> findById(UUID id);

    Optional<UserReadModel> findByEmail(String email);
}
