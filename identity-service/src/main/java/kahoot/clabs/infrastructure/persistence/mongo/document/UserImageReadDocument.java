package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "user_images")
@Getter
@Setter
@NoArgsConstructor
public class UserImageReadDocument {

    @BsonId
    private UUID id;

    private UUID userId;
    private String url;
    private String type;
    private String alt;
    private String slug;
    private Instant createdAt;
    private Instant updatedAt;
}
