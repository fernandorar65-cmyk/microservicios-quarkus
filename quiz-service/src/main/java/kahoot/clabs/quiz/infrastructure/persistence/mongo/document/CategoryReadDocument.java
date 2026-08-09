package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "categories")
@Getter
@Setter
@NoArgsConstructor
public class CategoryReadDocument {

    @BsonId
    private UUID id;

    private UUID organizationId;
    private String name;
    private String description;
    private String color;
    private String icon;
    private int quizCount;
    private Instant createdAt;
    private Instant updatedAt;
}
