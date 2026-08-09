package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizAnswerOptionEmbed {

    private UUID id;
    private String text;

    @BsonProperty("isCorrect")
    private boolean correct;

    private String explanation;
    private int orderIndex;
}
