package kahoot.clabs.application.usecase;

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.application.dto.UserRoleResponse;
import kahoot.clabs.application.port.read.RoleReadPort;
import kahoot.clabs.application.port.read.UserReadPort;
import kahoot.clabs.application.query.GetUserRolesQuery;
import kahoot.clabs.application.readmodel.UserReadModel;
import kahoot.clabs.domain.exception.UserNotFoundException;

@ApplicationScoped
public class GetUserRolesUseCase {

    private final UserReadPort userReadPort;
    private final RoleReadPort roleReadPort;

    public GetUserRolesUseCase(UserReadPort userReadPort, RoleReadPort roleReadPort) {
        this.userReadPort = userReadPort;
        this.roleReadPort = roleReadPort;
    }

    public List<UserRoleResponse> execute(GetUserRolesQuery query) {
        UserReadModel user = userReadPort.findById(query.userId())
                .orElseThrow(() -> new UserNotFoundException(query.userId()));

        if (user.getRole() == null || user.getRole().getId() == null) {
            return Collections.emptyList();
        }

        return roleReadPort.findById(user.getRole().getId())
                .map(role -> role.getPermissions().stream()
                        .map(permission -> new UserRoleResponse(permission.getName(), permission.getDescription()))
                        .toList())
                .orElse(Collections.emptyList());
    }
}
