package kahoot.clabs.application.port.write;

import kahoot.clabs.application.readmodel.PermissionReadModel;

public interface PermissionProjectionPort {

    void save(PermissionReadModel readModel);
}
