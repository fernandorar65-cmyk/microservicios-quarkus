package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationMemberEmbed {

    private UUID id;
    private UUID userId;
    private OrganizationMemberUserEmbed user;
    private UUID roleId;
    private String status;
    private Instant joinedAt;
}
