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
public class GameSessionReadModel {

    private UUID id;
    private UUID organizationId;
    private UUID quizId;
    private UUID hostUserId;
    private SessionQuiz quiz;
    private SessionHost host;
    private String status;
    private int currentQuestionIndex;
    private List<SessionPlayer> players = new ArrayList<>();
    private List<SessionQuestion> questions = new ArrayList<>();
    private List<PlayerAnswer> playerAnswers = new ArrayList<>();
    private int playerCount;
    private Instant startedAt;
    private Instant finishedAt;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SessionQuiz {

        private UUID id;
        private String title;
        private String thumbnailUrl;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SessionHost {

        private UUID id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SessionPlayer {

        private UUID id;
        private UUID userId;
        private String nickname;
        private int score;
        private boolean connected;
        private Instant joinedAt;
        private Instant leftAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SessionQuestion {

        private UUID id;
        private UUID sourceQuestionId;
        private int orderIndex;
        private int points;
        private int timeLimitSeconds;
        private String title;
        private String description;
        private String questionType;
        private Instant openedAt;
        private Instant closedAt;
        private List<SessionAnswerOption> answerOptions = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SessionAnswerOption {

        private UUID id;
        private UUID sourceAnswerOptionId;
        private String text;
        private boolean correct;
        private int orderIndex;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlayerAnswer {

        private UUID id;
        private UUID sessionQuestionId;
        private UUID sessionPlayerId;
        private UUID sessionAnswerOptionId;
        private boolean correct;
        private long responseTimeMs;
        private int awardedPoints;
        private Instant answeredAt;
    }
}
