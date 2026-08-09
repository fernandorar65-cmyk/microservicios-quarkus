package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRoleEmbed {

    private UUID id;
    private String name;
    private String type;
    private List<UserPermissionEmbed> permissions = new ArrayList<>();
}
