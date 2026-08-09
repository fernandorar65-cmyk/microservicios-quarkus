package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationReadModel {

    private UUID id;
    private String name;
    private String slug;
    private String logoUrl;
    private String description;
    private String timezone;
    private String language;
    private String status;
    private List<OrganizationMemberReadModel> members;
    private int memberCount;
    private Instant createdAt;
    private Instant updatedAt;
}
