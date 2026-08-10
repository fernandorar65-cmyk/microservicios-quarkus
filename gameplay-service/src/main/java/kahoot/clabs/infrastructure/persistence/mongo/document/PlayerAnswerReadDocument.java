package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "player_answers")
@Getter
@Setter
@NoArgsConstructor
public class PlayerAnswerReadDocument {

    @BsonId
    private UUID id;

    private UUID sessionQuestionId;
    private UUID sessionPlayerId;
    private UUID sessionAnswerOptionId;
    private Boolean isCorrect;
    private Long responseTimeMs;
    private Integer awardedPoints;
    private Instant answeredAt;
}
