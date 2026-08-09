package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RoleReadDocument {

    @BsonId
    private UUID id;

    private String name;
    private String type;
    private String description;
    private List<RolePermissionEmbed> permissions = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
}
