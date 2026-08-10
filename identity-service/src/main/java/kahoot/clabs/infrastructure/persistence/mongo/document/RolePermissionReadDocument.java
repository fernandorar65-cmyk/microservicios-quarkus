package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "role_permissions")
@Getter
@Setter
@NoArgsConstructor
public class RolePermissionReadDocument {

    @BsonId
    private String id;

    private UUID roleId;
    private UUID permissionId;
}
