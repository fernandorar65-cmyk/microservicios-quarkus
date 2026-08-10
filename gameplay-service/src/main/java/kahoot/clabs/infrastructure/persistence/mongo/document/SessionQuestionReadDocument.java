package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "session_questions")
@Getter
@Setter
@NoArgsConstructor
public class SessionQuestionReadDocument {

    @BsonId
    private UUID id;

    private UUID sessionId;
    private UUID sourceQuestionId;
    private Integer orderIndex;
    private Integer points;
    private Integer timeLimitSeconds;
    private String title;
    private String description;
    private String questionType;
    private Instant openedAt;
    private Instant closedAt;
}
