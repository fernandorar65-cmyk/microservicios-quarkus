package kahoot.clabs.application.port.read;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.application.readmodel.OrganizationReadModel;

public interface OrganizationReadPort {

    Optional<OrganizationReadModel> findById(UUID id);

    Optional<OrganizationReadModel> findBySlug(String slug);
}
