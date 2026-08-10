package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "quiz_categories")
@Getter
@Setter
@NoArgsConstructor
public class QuizCategoryReadDocument {

    @BsonId
    private String id;

    private UUID quizId;
    private UUID categoryId;
}
