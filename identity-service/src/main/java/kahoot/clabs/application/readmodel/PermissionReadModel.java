package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PermissionReadModel {

    private UUID id;
    private String name;
    private String description;
    private String module;
    private Instant createdAt;
    private Instant updatedAt;
}
