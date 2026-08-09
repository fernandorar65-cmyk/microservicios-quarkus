package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleReadModel {

    private UUID id;
    private String name;
    private String type;
    private String description;
    private List<RolePermissionReadModel> permissions = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
}
