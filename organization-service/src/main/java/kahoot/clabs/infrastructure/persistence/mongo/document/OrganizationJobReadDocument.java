package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "organization_jobs")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationJobReadDocument {

    @BsonId
    private UUID id;

    private String name;
    private String description;
}
