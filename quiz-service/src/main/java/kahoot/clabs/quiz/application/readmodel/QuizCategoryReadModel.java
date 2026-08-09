package kahoot.clabs.quiz.application.readmodel;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizCategoryReadModel {

    private UUID id;
    private String name;
    private String color;
    private String icon;
}
