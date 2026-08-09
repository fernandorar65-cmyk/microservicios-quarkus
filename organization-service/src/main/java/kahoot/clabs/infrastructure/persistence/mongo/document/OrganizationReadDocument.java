package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "organizations")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationReadDocument {

    @BsonId
    private UUID id;

    private String name;
    private String slug;
    private String logoUrl;
    private String description;
    private String timezone;
    private String language;
    private String status;
    private List<OrganizationMemberEmbed> members;
    private int memberCount;
    private Instant createdAt;
    private Instant updatedAt;
}
