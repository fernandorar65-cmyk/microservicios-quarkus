package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "organization_member_statuses")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationMemberStatusReadDocument {

    @BsonId
    private UUID id;

    private String name;
    private String description;
}
