package kahoot.clabs.application.port.out.read;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.application.readmodel.RoleReadModel;

public interface RoleReadPort {

    Optional<RoleReadModel> findById(UUID id);

    Optional<RoleReadModel> findByType(String type);
}
