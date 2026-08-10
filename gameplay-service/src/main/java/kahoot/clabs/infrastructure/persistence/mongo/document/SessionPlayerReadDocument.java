package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "session_players")
@Getter
@Setter
@NoArgsConstructor
public class SessionPlayerReadDocument {

    @BsonId
    private UUID id;

    private UUID sessionId;
    private UUID userId;
    private String nickname;
    private Integer score;
    private Boolean connected;
    private Instant joinedAt;
    private Instant leftAt;
}
