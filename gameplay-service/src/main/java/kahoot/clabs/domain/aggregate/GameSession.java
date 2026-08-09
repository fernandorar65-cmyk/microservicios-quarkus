package kahoot.clabs.domain.aggregate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.domain.entity.PlayerAnswer;
import kahoot.clabs.domain.entity.SessionAnswerOption;
import kahoot.clabs.domain.entity.SessionPlayer;
import kahoot.clabs.domain.entity.SessionQuestion;
import kahoot.clabs.domain.valueobject.Nickname;
import kahoot.clabs.domain.valueobject.SessionStatus;
import kahoot.clabs.domain.shared.AggregateRoot;
import kahoot.clabs.domain.shared.DomainException;

public class GameSession extends AggregateRoot {

    private final UUID organizationId;
    private final UUID quizId;
    private final UUID hostUserId;
    private SessionStatus status;
    // para saber la pregunta actual
    private int currentQuestionIndex;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private final List<SessionPlayer> players = new ArrayList<>();
    private final List<SessionQuestion> questions = new ArrayList<>();
    private final List<PlayerAnswer> answers = new ArrayList<>();

    private GameSession(
            UUID id,
            UUID organizationId,
            UUID quizId,
            UUID hostUserId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        if (organizationId == null) {
            throw new DomainException("Organization id is required");
        }
        if (quizId == null) {
            throw new DomainException("Quiz id is required");
        }
        if (hostUserId == null) {
            throw new DomainException("Host user id is required");
        }
        this.organizationId = organizationId;
        this.quizId = quizId;
        this.hostUserId = hostUserId;
        this.status = SessionStatus.LOBBY;
        this.currentQuestionIndex = 0;
    }

    public static GameSession create(UUID organizationId, UUID quizId, UUID hostUserId) {
        return new GameSession(null, organizationId, quizId, hostUserId, null, null);
    }

    public static GameSession rehydrate(
            UUID id,
            UUID organizationId,
            UUID quizId,
            UUID hostUserId,
            SessionStatus status,
            int currentQuestionIndex,
            LocalDateTime startedAt,
            LocalDateTime finishedAt,
            List<SessionPlayer> players,
            List<SessionQuestion> questions,
            List<PlayerAnswer> answers,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        GameSession session = new GameSession(id, organizationId, quizId, hostUserId, createdAt, updatedAt);
        session.status = status != null ? status : SessionStatus.LOBBY;
        session.currentQuestionIndex = currentQuestionIndex;
        session.startedAt = startedAt;
        session.finishedAt = finishedAt;
        if (players != null) {
            session.players.addAll(players);
        }
        if (questions != null) {
            session.questions.addAll(questions);
        }
        if (answers != null) {
            session.answers.addAll(answers);
        }
        return session;
    }

    public void freezeQuestions(List<SessionQuestion> frozenQuestions) {
        ensureSessionNotFinished();
        if (!questions.isEmpty()) {
            return;
        }
        if (frozenQuestions == null || frozenQuestions.isEmpty()) {
            throw new DomainException("Cannot start a session without questions");
        }
        frozenQuestions.stream()
                .sorted(Comparator.comparingInt(SessionQuestion::getOrderIndex))
                .forEach(question -> {
                    question.assignSessionId(getId());
                    questions.add(question);
                });
        touch();
    }

    public SessionPlayer join(UUID userId, String nickname) {
        ensureAllowsJoin();
        if (userId == null) {
            throw new DomainException("User id is required");
        }
        Nickname nick = Nickname.of(nickname);
        findPlayerByUserId(userId).ifPresent(existing -> {
            throw new DomainException("User already joined this session: " + userId);
        });
        boolean nicknameTaken = players.stream()
                .anyMatch(player -> player.getNickname().equals(nick));
        if (nicknameTaken) {
            throw new DomainException("Nickname already taken in this session: " + nick.value());
        }
        SessionPlayer player = SessionPlayer.join(getId(), userId, nick);
        players.add(player);
        touch();
        return player;
    }

    public void leave(UUID userId) {
        SessionPlayer player = requirePlayerByUserId(userId);
        player.leave();
        touch();
    }

    public void changeNickname(UUID userId, String nickname) {
        if (status != SessionStatus.LOBBY) {
            throw new DomainException("Nickname can only be changed while the session is in lobby");
        }
        Nickname nick = Nickname.of(nickname);
        SessionPlayer player = requirePlayerByUserId(userId);
        boolean taken = players.stream()
                .anyMatch(other -> !other.getUserId().equals(userId) && other.getNickname().equals(nick));
        if (taken) {
            throw new DomainException("Nickname already taken in this session: " + nick.value());
        }
        player.changeNickname(nick);
        touch();
    }

    public void start() {
        ensureHostActionAllowed();
        if (status != SessionStatus.LOBBY) {
            throw new DomainException("Only lobby sessions can be started");
        }
        if (questions.isEmpty()) {
            throw new DomainException("Session has no frozen questions; freeze the quiz snapshot before starting");
        }
        this.startedAt = LocalDateTime.now();
        this.currentQuestionIndex = 0;
        openCurrentQuestion();
        touch();
    }

    public void openQuestion(Integer questionIndex) {
        ensureHostActionAllowed();
        if (questions.isEmpty()) {
            throw new DomainException("Session has no questions");
        }
        if (status == SessionStatus.LOBBY) {
            throw new DomainException("Start the session before opening a question");
        }
        if (status == SessionStatus.QUESTION_OPEN) {
            throw new DomainException("A question is already open");
        }
        if (status != SessionStatus.QUESTION_RESULT && status != SessionStatus.QUESTION_OPEN) {
            throw new DomainException("Cannot open a question in status " + status);
        }
        if (questionIndex != null) {
            if (questionIndex < 0 || questionIndex >= questions.size()) {
                throw new DomainException("Question index out of range: " + questionIndex);
            }
            this.currentQuestionIndex = questionIndex;
        }
        openCurrentQuestion();
        touch();
    }

    public void closeQuestion() {
        ensureHostActionAllowed();
        if (status != SessionStatus.QUESTION_OPEN) {
            throw new DomainException("No open question to close");
        }
        SessionQuestion current = requireCurrentQuestion();
        current.close();
        this.status = SessionStatus.QUESTION_RESULT;
        touch();
    }

    public void nextQuestion() {
        ensureHostActionAllowed();
        if (status != SessionStatus.QUESTION_RESULT) {
            throw new DomainException("Can only advance after showing question results");
        }
        int nextIndex = currentQuestionIndex + 1;
        if (nextIndex >= questions.size()) {
            finish();
            return;
        }
        this.currentQuestionIndex = nextIndex;
        openCurrentQuestion();
        touch();
    }

    public PlayerAnswer submitAnswer(UUID userId, UUID sessionAnswerOptionId) {
        if (status != SessionStatus.QUESTION_OPEN) {
            throw new DomainException("Answers are only accepted while a question is open");
        }
        SessionPlayer player = requirePlayerByUserId(userId);
        if (!player.isConnected()) {
            throw new DomainException("Disconnected players cannot answer");
        }
        SessionQuestion question = requireCurrentQuestion();
        boolean alreadyAnswered = answers.stream()
                .anyMatch(answer -> answer.getSessionQuestionId().equals(question.getId())
                        && answer.getSessionPlayerId().equals(player.getId()));
        if (alreadyAnswered) {
            throw new DomainException("Player already answered this question");
        }

        boolean correct = false;
        if (sessionAnswerOptionId != null) {
            SessionAnswerOption option = question.findOption(sessionAnswerOptionId)
                    .orElseThrow(() -> new DomainException("Answer option not found in current question"));
            correct = option.isCorrect();
        }

        long responseTimeMs = 0L;
        if (question.getOpenedAt() != null) {
            responseTimeMs = Math.max(0L, Duration.between(question.getOpenedAt(), LocalDateTime.now()).toMillis());
        }
        int awardedPoints = correct ? question.getPoints() : 0;

        PlayerAnswer answer = PlayerAnswer.submit(
                question.getId(),
                player.getId(),
                sessionAnswerOptionId,
                correct,
                responseTimeMs,
                awardedPoints);
        answers.add(answer);
        if (awardedPoints > 0) {
            player.addScore(awardedPoints);
        }
        touch();
        return answer;
    }

    public void finish() {
        ensureHostActionAllowed();
        if (status.isTerminal()) {
            throw new DomainException("Session is already " + status);
        }
        if (status == SessionStatus.QUESTION_OPEN) {
            requireCurrentQuestion().close();
        }
        this.status = SessionStatus.FINISHED;
        this.finishedAt = LocalDateTime.now();
        touch();
    }

    public void cancel() {
        if (status == SessionStatus.FINISHED) {
            throw new DomainException("A finished session cannot be cancelled");
        }
        if (status == SessionStatus.CANCELLED) {
            return;
        }
        this.status = SessionStatus.CANCELLED;
        this.finishedAt = LocalDateTime.now();
        touch();
    }

    public void ensureBelongsTo(UUID organizationId) {
        if (!this.organizationId.equals(organizationId)) {
            throw new DomainException("Game session does not belong to organization: " + organizationId);
        }
    }

    public void ensureHost(UUID userId) {
        if (!hostUserId.equals(userId)) {
            throw new DomainException("Only the host can perform this action");
        }
    }

    public Optional<SessionQuestion> findCurrentQuestion() {
        return questions.stream()
                .filter(question -> question.getOrderIndex() == currentQuestionIndex)
                .findFirst();
    }

    public Optional<SessionQuestion> findQuestionById(UUID sessionQuestionId) {
        return questions.stream().filter(question -> question.getId().equals(sessionQuestionId)).findFirst();
    }

    public Optional<SessionPlayer> findPlayerByUserId(UUID userId) {
        return players.stream().filter(player -> player.getUserId().equals(userId)).findFirst();
    }

    public List<SessionPlayer> leaderboard() {
        return players.stream()
                .sorted(Comparator.comparingInt(SessionPlayer::getScore)
                        .reversed()
                        .thenComparing(SessionPlayer::getJoinedAt))
                .toList();
    }

    public List<PlayerAnswer> answersForPlayer(UUID sessionPlayerId) {
        return answers.stream()
                .filter(answer -> answer.getSessionPlayerId().equals(sessionPlayerId))
                .toList();
    }

    public List<PlayerAnswer> answersForQuestion(UUID sessionQuestionId) {
        return answers.stream()
                .filter(answer -> answer.getSessionQuestionId().equals(sessionQuestionId))
                .toList();
    }

    private void openCurrentQuestion() {
        SessionQuestion question = requireCurrentQuestion();
        question.open();
        this.status = SessionStatus.QUESTION_OPEN;
    }

    private SessionQuestion requireCurrentQuestion() {
        return findCurrentQuestion()
                .orElseThrow(() -> new DomainException(
                        "No question at index " + currentQuestionIndex));
    }

    private SessionPlayer requirePlayerByUserId(UUID userId) {
        return findPlayerByUserId(userId)
                .orElseThrow(() -> new DomainException("Player not found in session: " + userId));
    }

    private void ensureAllowsJoin() {
        if (!status.allowsJoin()) {
            throw new DomainException("Players can only join while the session is in lobby");
        }
    }

    private void ensureHostActionAllowed() {
        ensureSessionNotFinished();
    }

    private void ensureSessionNotFinished() {
        if (status.isTerminal()) {
            throw new DomainException("Session is already " + status);
        }
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public List<SessionPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public List<SessionQuestion> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public List<PlayerAnswer> getAnswers() {
        return Collections.unmodifiableList(answers);
    }
}
