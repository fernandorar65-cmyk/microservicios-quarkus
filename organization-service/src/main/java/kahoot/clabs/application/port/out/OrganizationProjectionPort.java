package kahoot.clabs.application.port.out;

import java.util.UUID;

import kahoot.clabs.application.readmodel.OrganizationReadModel;

/**
 * Port for synchronizing organization read models after write-side changes.
 */
public interface OrganizationProjectionPort {

    void save(OrganizationReadModel readModel);

    void deleteById(UUID id);
}
