package kahoot.clabs.infrastructure.persistence.mongo.document;

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
public class SessionQuestionEmbed {

    private UUID id;
    private UUID sourceQuestionId;
    private int orderIndex;
    private int points;
    private int timeLimitSeconds;
    private String title;
    private String description;
    private String questionType;
    private Instant openedAt;
    private Instant closedAt;
    private List<SessionAnswerOptionEmbed> answerOptions = new ArrayList<>();
}
