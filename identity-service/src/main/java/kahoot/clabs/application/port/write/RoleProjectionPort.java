package kahoot.clabs.application.port.write;

import kahoot.clabs.application.readmodel.RoleReadModel;

public interface RoleProjectionPort {

    void save(RoleReadModel readModel);
}
