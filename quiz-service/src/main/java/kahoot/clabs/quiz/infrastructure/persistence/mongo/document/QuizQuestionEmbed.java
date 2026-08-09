package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizQuestionEmbed {

    private UUID id;
    private String title;
    private String description;
    private String type;
    private String difficulty;
    private String explanation;
    private int orderIndex;
    private int timeLimitSeconds;
    private int points;
    private QuizAssetEmbed asset;
    private List<QuizAnswerOptionEmbed> answerOptions = new ArrayList<>();
}
