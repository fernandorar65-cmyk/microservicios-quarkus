package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "question_assets")
@Getter
@Setter
@NoArgsConstructor
public class QuestionAssetReadDocument {

    @BsonId
    private UUID id;

    private UUID questionId;
    private String type;
    private String url;
    private String thumbnailUrl;
    private String altText;
    private Integer durationSeconds;
    private Instant createdAt;
    private Instant updatedAt;
}
