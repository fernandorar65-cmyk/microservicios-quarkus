package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizSettingsEmbed {

    private boolean randomQuestions;
    private boolean randomAnswers;
    private boolean showCorrectAnswer;
    private boolean showRanking;
    private boolean allowRetry;
    private boolean showTimer;
    private boolean musicEnabled;
}
