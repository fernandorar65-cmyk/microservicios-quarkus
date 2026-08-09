package kahoot.clabs.application.readmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRoleReadModel {

    private UUID id;
    private String name;
    private String type;
    private List<UserPermissionReadModel> permissions = new ArrayList<>();
}
