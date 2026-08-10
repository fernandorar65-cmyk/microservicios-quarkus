package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "questions")
@Getter
@Setter
@NoArgsConstructor
public class QuestionReadDocument {

    @BsonId
    private UUID id;

    private UUID quizId;
    private String title;
    private String description;
    private String type;
    private String difficulty;
    private String explanation;
    private Integer orderIndex;
    private Integer timeLimitSeconds;
    private Integer points;
    private Instant createdAt;
    private Instant updatedAt;
}
