package kahoot.clabs.application.port.write;

import kahoot.clabs.application.readmodel.PermissionReadModel;

/**
 * Upserts permission read models (Mongo).
 */
public interface PermissionProjectionPort {

    void save(PermissionReadModel readModel);
}
