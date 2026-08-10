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
public class PlayableQuestionEmbed {

    private UUID id;
    private int orderIndex;
    private int points;
    private int timeLimitSeconds;
    private String title;
    private String description;
    private String type;
    private List<PlayableAnswerOptionEmbed> options = new ArrayList<>();
}
