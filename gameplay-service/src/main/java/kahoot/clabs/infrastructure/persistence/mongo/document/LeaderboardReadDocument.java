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

@MongoEntity(collection = "leaderboards")
@Getter
@Setter
@NoArgsConstructor
public class LeaderboardReadDocument {

    @BsonId
    private UUID id;

    private UUID sessionId;
    private UUID organizationId;
    private Instant updatedAt;
    private List<LeaderboardEntryEmbed> ranking = new ArrayList<>();
}
