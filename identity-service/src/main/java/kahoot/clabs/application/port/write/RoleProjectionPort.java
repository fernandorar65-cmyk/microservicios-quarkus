package kahoot.clabs.application.port.write;

import kahoot.clabs.application.readmodel.RoleReadModel;

/**
 * Upserts role read models (Mongo).
 */
public interface RoleProjectionPort {

    void save(RoleReadModel readModel);
}
