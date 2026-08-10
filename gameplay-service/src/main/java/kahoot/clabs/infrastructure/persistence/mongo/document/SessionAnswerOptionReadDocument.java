package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "session_answer_options")
@Getter
@Setter
@NoArgsConstructor
public class SessionAnswerOptionReadDocument {

    @BsonId
    private UUID id;

    private UUID sessionQuestionId;
    private UUID sourceAnswerOptionId;
    private String text;
    private Boolean isCorrect;
    private Integer orderIndex;
}
