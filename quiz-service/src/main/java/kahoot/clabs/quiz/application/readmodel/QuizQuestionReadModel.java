package kahoot.clabs.quiz.application.readmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizQuestionReadModel {

    private UUID id;
    private String title;
    private String description;
    private String type;
    private String difficulty;
    private String explanation;
    private int orderIndex;
    private int timeLimitSeconds;
    private int points;
    private QuizAssetReadModel asset;
    private List<QuizAnswerOptionReadModel> answerOptions = new ArrayList<>();
}
