package kahoot.clabs.quiz.application.readmodel;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizAssetReadModel {

    private UUID id;
    private String type;
    private String url;
    private String thumbnailUrl;
    private String altText;
    private Integer durationSeconds;
}
