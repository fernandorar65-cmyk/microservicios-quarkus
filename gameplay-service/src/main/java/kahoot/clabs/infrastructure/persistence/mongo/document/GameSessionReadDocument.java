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

@MongoEntity(collection = "game_sessions")
@Getter
@Setter
@NoArgsConstructor
public class GameSessionReadDocument {

    @BsonId
    private UUID id;

    private UUID organizationId;
    private UUID quizId;
    private UUID hostUserId;
    private SessionQuizEmbed quiz;
    private SessionHostEmbed host;
    private String status;
    private int currentQuestionIndex;
    private List<SessionPlayerEmbed> players = new ArrayList<>();
    private List<SessionQuestionEmbed> questions = new ArrayList<>();
    private List<PlayerAnswerEmbed> playerAnswers = new ArrayList<>();
    private int playerCount;
    private Instant startedAt;
    private Instant finishedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
