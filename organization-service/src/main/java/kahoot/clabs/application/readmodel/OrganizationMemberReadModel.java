package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationMemberReadModel {

    private UUID id;
    private UUID userId;
    private OrganizationMemberUserReadModel user;
    private UUID roleId;
    private String status;
    private Instant joinedAt;
}
