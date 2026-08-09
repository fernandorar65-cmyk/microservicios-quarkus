package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "quizzes")
@Getter
@Setter
@NoArgsConstructor
public class QuizReadDocument {

    @BsonId
    private UUID id;

    private UUID organizationId;
    private UUID createdBy;
    private QuizCreatorEmbed creator;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String status;
    private String difficulty;
    private Integer estimatedTimeMinutes;
    private int playCount;
    private BigDecimal averageRating;

    @BsonProperty("isTemplate")
    private boolean template;

    private QuizSettingsEmbed settings;
    private List<QuizCategoryEmbed> categories = new ArrayList<>();
    private List<QuizQuestionEmbed> questions = new ArrayList<>();
    private int questionCount;
    private Instant createdAt;
    private Instant updatedAt;
}
