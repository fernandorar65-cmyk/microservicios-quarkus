package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LeaderboardReadModel {

    private UUID id;
    private UUID sessionId;
    private UUID organizationId;
    private Instant updatedAt;
    private List<LeaderboardEntry> ranking = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LeaderboardEntry {

        private int position;
        private UUID sessionPlayerId;
        private UUID userId;
        private String nickname;
        private int score;
    }
}
