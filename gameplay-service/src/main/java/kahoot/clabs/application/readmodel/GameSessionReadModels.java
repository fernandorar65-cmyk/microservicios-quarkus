package kahoot.clabs.application.readmodel;

import java.util.Collections;
import java.util.List;

import kahoot.clabs.application.event.GameSessionProjectionSnapshot;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot.AnswerOptionPayload;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot.AnswerPayload;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot.HostPayload;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot.LeaderboardEntryPayload;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot.PlayerPayload;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot.QuestionPayload;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot.QuizPayload;

public final class GameSessionReadModels {

    private GameSessionReadModels() {
    }

    public static GameSessionReadModel from(GameSessionProjectionSnapshot snapshot) {
        GameSessionReadModel model = new GameSessionReadModel();
        model.setId(snapshot.sessionId());
        model.setOrganizationId(snapshot.organizationId());
        model.setQuizId(snapshot.quizId());
        model.setHostUserId(snapshot.hostUserId());
        model.setQuiz(toQuiz(snapshot.quiz()));
        model.setHost(toHost(snapshot.host()));
        model.setStatus(snapshot.status());
        model.setCurrentQuestionIndex(snapshot.currentQuestionIndex());
        model.setPlayers(toPlayers(snapshot.players()));
        model.setQuestions(toQuestions(snapshot.questions()));
        model.setPlayerAnswers(toAnswers(snapshot.playerAnswers()));
        model.setPlayerCount(snapshot.playerCount());
        model.setStartedAt(snapshot.startedAt());
        model.setFinishedAt(snapshot.finishedAt());
        model.setCreatedAt(snapshot.createdAt());
        model.setUpdatedAt(snapshot.updatedAt());
        return model;
    }

    public static LeaderboardReadModel toLeaderboard(GameSessionProjectionSnapshot snapshot) {
        LeaderboardReadModel model = new LeaderboardReadModel();
        model.setId(snapshot.sessionId());
        model.setSessionId(snapshot.sessionId());
        model.setOrganizationId(snapshot.organizationId());
        model.setUpdatedAt(snapshot.updatedAt());
        model.setRanking(toRanking(snapshot.leaderboardRanking()));
        return model;
    }

    private static GameSessionReadModel.SessionQuiz toQuiz(QuizPayload quiz) {
        if (quiz == null) {
            return null;
        }
        GameSessionReadModel.SessionQuiz model = new GameSessionReadModel.SessionQuiz();
        model.setId(quiz.id());
        model.setTitle(quiz.title());
        model.setThumbnailUrl(quiz.thumbnailUrl());
        return model;
    }

    private static GameSessionReadModel.SessionHost toHost(HostPayload host) {
        if (host == null) {
            return null;
        }
        GameSessionReadModel.SessionHost model = new GameSessionReadModel.SessionHost();
        model.setId(host.id());
        model.setName(host.name());
        return model;
    }

    private static List<GameSessionReadModel.SessionPlayer> toPlayers(List<PlayerPayload> players) {
        if (players == null || players.isEmpty()) {
            return Collections.emptyList();
        }
        return players.stream().map(GameSessionReadModels::toPlayer).toList();
    }

    private static GameSessionReadModel.SessionPlayer toPlayer(PlayerPayload player) {
        GameSessionReadModel.SessionPlayer model = new GameSessionReadModel.SessionPlayer();
        model.setId(player.id());
        model.setUserId(player.userId());
        model.setNickname(player.nickname());
        model.setScore(player.score());
        model.setConnected(player.connected());
        model.setJoinedAt(player.joinedAt());
        model.setLeftAt(player.leftAt());
        return model;
    }

    private static List<GameSessionReadModel.SessionQuestion> toQuestions(List<QuestionPayload> questions) {
        if (questions == null || questions.isEmpty()) {
            return Collections.emptyList();
        }
        return questions.stream().map(GameSessionReadModels::toQuestion).toList();
    }

    private static GameSessionReadModel.SessionQuestion toQuestion(QuestionPayload question) {
        GameSessionReadModel.SessionQuestion model = new GameSessionReadModel.SessionQuestion();
        model.setId(question.id());
        model.setSourceQuestionId(question.sourceQuestionId());
        model.setOrderIndex(question.orderIndex());
        model.setPoints(question.points());
        model.setTimeLimitSeconds(question.timeLimitSeconds());
        model.setTitle(question.title());
        model.setDescription(question.description());
        model.setQuestionType(question.questionType());
        model.setOpenedAt(question.openedAt());
        model.setClosedAt(question.closedAt());
        model.setAnswerOptions(toOptions(question.answerOptions()));
        return model;
    }

    private static List<GameSessionReadModel.SessionAnswerOption> toOptions(List<AnswerOptionPayload> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }
        return options.stream().map(GameSessionReadModels::toOption).toList();
    }

    private static GameSessionReadModel.SessionAnswerOption toOption(AnswerOptionPayload option) {
        GameSessionReadModel.SessionAnswerOption model = new GameSessionReadModel.SessionAnswerOption();
        model.setId(option.id());
        model.setSourceAnswerOptionId(option.sourceAnswerOptionId());
        model.setText(option.text());
        model.setCorrect(option.correct());
        model.setOrderIndex(option.orderIndex());
        return model;
    }

    private static List<GameSessionReadModel.PlayerAnswer> toAnswers(List<AnswerPayload> answers) {
        if (answers == null || answers.isEmpty()) {
            return Collections.emptyList();
        }
        return answers.stream().map(GameSessionReadModels::toAnswer).toList();
    }

    private static GameSessionReadModel.PlayerAnswer toAnswer(AnswerPayload answer) {
        GameSessionReadModel.PlayerAnswer model = new GameSessionReadModel.PlayerAnswer();
        model.setId(answer.id());
        model.setSessionQuestionId(answer.sessionQuestionId());
        model.setSessionPlayerId(answer.sessionPlayerId());
        model.setSessionAnswerOptionId(answer.sessionAnswerOptionId());
        model.setCorrect(answer.correct());
        model.setResponseTimeMs(answer.responseTimeMs());
        model.setAwardedPoints(answer.awardedPoints());
        model.setAnsweredAt(answer.answeredAt());
        return model;
    }

    private static List<LeaderboardReadModel.LeaderboardEntry> toRanking(List<LeaderboardEntryPayload> ranking) {
        if (ranking == null || ranking.isEmpty()) {
            return Collections.emptyList();
        }
        return ranking.stream().map(entry -> {
            LeaderboardReadModel.LeaderboardEntry model = new LeaderboardReadModel.LeaderboardEntry();
            model.setPosition(entry.position());
            model.setSessionPlayerId(entry.sessionPlayerId());
            model.setUserId(entry.userId());
            model.setNickname(entry.nickname());
            model.setScore(entry.score());
            return model;
        }).toList();
    }
}
