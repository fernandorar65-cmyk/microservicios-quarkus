package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import kahoot.clabs.application.port.write.GameSessionProjectionPort;
import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.application.readmodel.LeaderboardReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.document.GameSessionReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.LeaderboardEntryEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.LeaderboardReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.PlayerAnswerEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.SessionAnswerOptionEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.SessionHostEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.SessionPlayerEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.SessionQuestionEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.SessionQuizEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.repository.GameSessionMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.repository.LeaderboardMongoRepository;

@ApplicationScoped
public class GameSessionProjectionAdapter implements GameSessionProjectionPort {

    private final GameSessionMongoRepository gameSessionMongoRepository;
    private final LeaderboardMongoRepository leaderboardMongoRepository;

    @Inject
    public GameSessionProjectionAdapter(
            GameSessionMongoRepository gameSessionMongoRepository,
            LeaderboardMongoRepository leaderboardMongoRepository) {
        this.gameSessionMongoRepository = gameSessionMongoRepository;
        this.leaderboardMongoRepository = leaderboardMongoRepository;
    }

    @Override
    @Transactional(TxType.NOT_SUPPORTED)
    public void save(GameSessionReadModel session, LeaderboardReadModel leaderboard) {
        gameSessionMongoRepository.persistOrUpdate(toSessionDocument(session));
        leaderboardMongoRepository.persistOrUpdate(toLeaderboardDocument(leaderboard));
    }

    private GameSessionReadDocument toSessionDocument(GameSessionReadModel model) {
        GameSessionReadDocument document = new GameSessionReadDocument();
        document.setId(model.getId());
        document.setOrganizationId(model.getOrganizationId());
        document.setQuizId(model.getQuizId());
        document.setHostUserId(model.getHostUserId());
        document.setQuiz(toQuizEmbed(model.getQuiz()));
        document.setHost(toHostEmbed(model.getHost()));
        document.setStatus(model.getStatus());
        document.setCurrentQuestionIndex(model.getCurrentQuestionIndex());
        document.setPlayers(toPlayerEmbeds(model.getPlayers()));
        document.setQuestions(toQuestionEmbeds(model.getQuestions()));
        document.setPlayerAnswers(toAnswerEmbeds(model.getPlayerAnswers()));
        document.setPlayerCount(model.getPlayerCount());
        document.setStartedAt(model.getStartedAt());
        document.setFinishedAt(model.getFinishedAt());
        document.setCreatedAt(model.getCreatedAt());
        document.setUpdatedAt(model.getUpdatedAt());
        return document;
    }

    private LeaderboardReadDocument toLeaderboardDocument(LeaderboardReadModel model) {
        LeaderboardReadDocument document = new LeaderboardReadDocument();
        document.setId(model.getId() != null ? model.getId() : model.getSessionId());
        document.setSessionId(model.getSessionId());
        document.setOrganizationId(model.getOrganizationId());
        document.setUpdatedAt(model.getUpdatedAt());
        document.setRanking(toRankingEmbeds(model.getRanking()));
        return document;
    }

    private SessionQuizEmbed toQuizEmbed(GameSessionReadModel.SessionQuiz quiz) {
        if (quiz == null) {
            return null;
        }
        SessionQuizEmbed embed = new SessionQuizEmbed();
        embed.setId(quiz.getId());
        embed.setTitle(quiz.getTitle());
        embed.setThumbnailUrl(quiz.getThumbnailUrl());
        return embed;
    }

    private SessionHostEmbed toHostEmbed(GameSessionReadModel.SessionHost host) {
        if (host == null) {
            return null;
        }
        SessionHostEmbed embed = new SessionHostEmbed();
        embed.setId(host.getId());
        embed.setName(host.getName());
        return embed;
    }

    private List<SessionPlayerEmbed> toPlayerEmbeds(List<GameSessionReadModel.SessionPlayer> players) {
        if (players == null || players.isEmpty()) {
            return Collections.emptyList();
        }
        return players.stream().map(player -> {
            SessionPlayerEmbed embed = new SessionPlayerEmbed();
            embed.setId(player.getId());
            embed.setUserId(player.getUserId());
            embed.setNickname(player.getNickname());
            embed.setScore(player.getScore());
            embed.setConnected(player.isConnected());
            embed.setJoinedAt(player.getJoinedAt());
            embed.setLeftAt(player.getLeftAt());
            return embed;
        }).toList();
    }

    private List<SessionQuestionEmbed> toQuestionEmbeds(List<GameSessionReadModel.SessionQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            return Collections.emptyList();
        }
        return questions.stream().map(question -> {
            SessionQuestionEmbed embed = new SessionQuestionEmbed();
            embed.setId(question.getId());
            embed.setSourceQuestionId(question.getSourceQuestionId());
            embed.setOrderIndex(question.getOrderIndex());
            embed.setPoints(question.getPoints());
            embed.setTimeLimitSeconds(question.getTimeLimitSeconds());
            embed.setTitle(question.getTitle());
            embed.setDescription(question.getDescription());
            embed.setQuestionType(question.getQuestionType());
            embed.setOpenedAt(question.getOpenedAt());
            embed.setClosedAt(question.getClosedAt());
            embed.setAnswerOptions(toOptionEmbeds(question.getAnswerOptions()));
            return embed;
        }).toList();
    }

    private List<SessionAnswerOptionEmbed> toOptionEmbeds(
            List<GameSessionReadModel.SessionAnswerOption> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }
        return options.stream().map(option -> {
            SessionAnswerOptionEmbed embed = new SessionAnswerOptionEmbed();
            embed.setId(option.getId());
            embed.setSourceAnswerOptionId(option.getSourceAnswerOptionId());
            embed.setText(option.getText());
            embed.setCorrect(option.isCorrect());
            embed.setOrderIndex(option.getOrderIndex());
            return embed;
        }).toList();
    }

    private List<PlayerAnswerEmbed> toAnswerEmbeds(List<GameSessionReadModel.PlayerAnswer> answers) {
        if (answers == null || answers.isEmpty()) {
            return Collections.emptyList();
        }
        return answers.stream().map(answer -> {
            PlayerAnswerEmbed embed = new PlayerAnswerEmbed();
            embed.setId(answer.getId());
            embed.setSessionQuestionId(answer.getSessionQuestionId());
            embed.setSessionPlayerId(answer.getSessionPlayerId());
            embed.setSessionAnswerOptionId(answer.getSessionAnswerOptionId());
            embed.setCorrect(answer.isCorrect());
            embed.setResponseTimeMs(answer.getResponseTimeMs());
            embed.setAwardedPoints(answer.getAwardedPoints());
            embed.setAnsweredAt(answer.getAnsweredAt());
            return embed;
        }).toList();
    }

    private List<LeaderboardEntryEmbed> toRankingEmbeds(List<LeaderboardReadModel.LeaderboardEntry> ranking) {
        if (ranking == null || ranking.isEmpty()) {
            return Collections.emptyList();
        }
        return ranking.stream().map(entry -> {
            LeaderboardEntryEmbed embed = new LeaderboardEntryEmbed();
            embed.setPosition(entry.getPosition());
            embed.setSessionPlayerId(entry.getSessionPlayerId());
            embed.setUserId(entry.getUserId());
            embed.setNickname(entry.getNickname());
            embed.setScore(entry.getScore());
            return embed;
        }).toList();
    }
}
