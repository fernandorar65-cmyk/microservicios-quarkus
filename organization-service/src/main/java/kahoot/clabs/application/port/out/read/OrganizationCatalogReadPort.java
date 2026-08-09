package kahoot.clabs.application.port.out.read;

import java.util.Optional;

import kahoot.clabs.application.readmodel.OrganizationCatalogReadModel;

public interface OrganizationCatalogReadPort {

    Optional<OrganizationCatalogReadModel> findCatalog();
}
