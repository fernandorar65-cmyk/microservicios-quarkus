package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayableAnswerOptionEmbed {

    private UUID id;
    private String text;
    private boolean correct;
    private int orderIndex;
}
