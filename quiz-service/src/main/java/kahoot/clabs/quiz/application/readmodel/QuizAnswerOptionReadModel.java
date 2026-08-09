package kahoot.clabs.quiz.application.readmodel;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizAnswerOptionReadModel {

    private UUID id;
    private String text;
    private boolean correct;
    private String explanation;
    private int orderIndex;
}
