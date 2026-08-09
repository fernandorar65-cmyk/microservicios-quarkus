package kahoot.clabs.quiz.application.readmodel;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryReadModel {

    private UUID id;
    private UUID organizationId;
    private String name;
    private String description;
    private String color;
    private String icon;
    private int quizCount;
    private Instant createdAt;
    private Instant updatedAt;
}
