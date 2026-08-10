package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "permissions")
@Getter
@Setter
@NoArgsConstructor
public class PermissionReadDocument {

    @BsonId
    private UUID id;

    private String name;
    private String description;
    private String module;
    private Instant createdAt;
    private Instant updatedAt;
}
