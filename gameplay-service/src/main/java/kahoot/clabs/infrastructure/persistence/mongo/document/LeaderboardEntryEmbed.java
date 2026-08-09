package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LeaderboardEntryEmbed {

    private int position;
    private UUID sessionPlayerId;
    private UUID userId;
    private String nickname;
    private int score;
}
