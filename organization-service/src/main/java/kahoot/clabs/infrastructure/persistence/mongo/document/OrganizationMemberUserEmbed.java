package kahoot.clabs.infrastructure.persistence.mongo.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationMemberUserEmbed {

    private String fullName;
    private String email;
}
