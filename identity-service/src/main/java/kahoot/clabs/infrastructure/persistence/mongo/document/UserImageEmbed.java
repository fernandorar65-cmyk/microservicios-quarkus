package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserImageEmbed {

    private UUID id;
    private String url;
    private String type;
    private String alt;
    private String slug;
}
