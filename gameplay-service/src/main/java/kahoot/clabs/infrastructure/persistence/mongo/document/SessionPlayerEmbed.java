package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SessionPlayerEmbed {

    private UUID id;
    private UUID userId;
    private String nickname;
    private int score;
    private boolean connected;
    private Instant joinedAt;
    private Instant leftAt;
}
