package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "answer_options")
@Getter
@Setter
@NoArgsConstructor
public class AnswerOptionReadDocument {

    @BsonId
    private UUID id;

    private UUID questionId;
    private String text;
    private Boolean isCorrect;
    private String explanation;
    private Integer orderIndex;
    private Instant createdAt;
    private Instant updatedAt;
}
