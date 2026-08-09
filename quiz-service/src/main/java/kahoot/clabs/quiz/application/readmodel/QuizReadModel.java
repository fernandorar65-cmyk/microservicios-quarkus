package kahoot.clabs.quiz.application.readmodel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizReadModel {

    private UUID id;
    private UUID organizationId;
    private UUID createdBy;
    private QuizCreatorReadModel creator;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String status;
    private String difficulty;
    private Integer estimatedTimeMinutes;
    private int playCount;
    private BigDecimal averageRating;
    private boolean template;
    private QuizSettingsReadModel settings;
    private List<QuizCategoryReadModel> categories = new ArrayList<>();
    private List<QuizQuestionReadModel> questions = new ArrayList<>();
    private int questionCount;
    private Instant createdAt;
    private Instant updatedAt;
}
