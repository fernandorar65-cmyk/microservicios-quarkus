package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizCategoryEmbed {

    private UUID id;
    private String name;
    private String color;
    private String icon;
}
