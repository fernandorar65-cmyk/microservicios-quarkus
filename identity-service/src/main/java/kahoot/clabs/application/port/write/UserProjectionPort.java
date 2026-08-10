package kahoot.clabs.application.port.write;
import java.util.UUID;
import kahoot.clabs.application.readmodel.UserReadModel;

public interface UserProjectionPort {

    void save(UserReadModel readModel);

    void deleteById(UUID id);
}
