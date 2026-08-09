package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerAnswerEmbed {

    private UUID id;
    private UUID sessionQuestionId;
    private UUID sessionPlayerId;
    private UUID sessionAnswerOptionId;
    private boolean correct;
    private long responseTimeMs;
    private int awardedPoints;
    private Instant answeredAt;
}
