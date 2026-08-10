package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "organization_members")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationMemberReadDocument {

    @BsonId
    private UUID id;

    private UUID organizationId;
    private UUID userId;
    private UUID roleId;
    private String status;
    private Instant joinedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private OrganizationMemberUserEmbed user;
}
