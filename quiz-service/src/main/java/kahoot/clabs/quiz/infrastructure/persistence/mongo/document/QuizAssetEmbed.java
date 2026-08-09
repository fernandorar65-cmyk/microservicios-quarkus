package kahoot.clabs.quiz.infrastructure.persistence.mongo.document;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizAssetEmbed {

    private UUID id;
    private String type;
    private String url;
    private String thumbnailUrl;
    private String altText;
    private Integer durationSeconds;
}
