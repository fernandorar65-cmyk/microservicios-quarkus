package kahoot.clabs.application.port.write;

import java.util.UUID;

import kahoot.clabs.application.readmodel.OrganizationReadModel;

public interface OrganizationProjectionPort {

    void save(OrganizationReadModel readModel);

    void deleteById(UUID id);
}
