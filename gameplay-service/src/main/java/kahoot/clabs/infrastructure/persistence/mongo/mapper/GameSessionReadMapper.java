package kahoot.clabs.infrastructure.persistence.mongo.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;

import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.document.GameSessionReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.PlayerAnswerEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.SessionAnswerOptionEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.SessionHostEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.SessionPlayerEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.SessionQuestionEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.SessionQuizEmbed;

@ApplicationScoped
public class GameSessionReadMapper {

    public GameSessionReadModel toReadModel(GameSessionReadDocument document) {
        if (document == null) {
            return null;
        }

        GameSessionReadModel model = new GameSessionReadModel();
        model.setId(document.getId());
        model.setOrganizationId(document.getOrganizationId());
        model.setQuizId(document.getQuizId());
        model.setHostUserId(document.getHostUserId());
        model.setQuiz(toQuiz(document.getQuiz()));
        model.setHost(toHost(document.getHost()));
        model.setStatus(document.getStatus());
        model.setCurrentQuestionIndex(document.getCurrentQuestionIndex());
        model.setPlayers(toPlayers(document.getPlayers()));
        model.setQuestions(toQuestions(document.getQuestions()));
        model.setPlayerAnswers(toPlayerAnswers(document.getPlayerAnswers()));
        model.setPlayerCount(document.getPlayerCount());
        model.setStartedAt(document.getStartedAt());
        model.setFinishedAt(document.getFinishedAt());
        model.setCreatedAt(document.getCreatedAt());
        model.setUpdatedAt(document.getUpdatedAt());
        return model;
    }

    private GameSessionReadModel.SessionQuiz toQuiz(SessionQuizEmbed embed) {
        if (embed == null) {
            return null;
        }

        GameSessionReadModel.SessionQuiz quiz = new GameSessionReadModel.SessionQuiz();
        quiz.setId(embed.getId());
        quiz.setTitle(embed.getTitle());
        quiz.setThumbnailUrl(embed.getThumbnailUrl());
        return quiz;
    }

    private GameSessionReadModel.SessionHost toHost(SessionHostEmbed embed) {
        if (embed == null) {
            return null;
        }

        GameSessionReadModel.SessionHost host = new GameSessionReadModel.SessionHost();
        host.setId(embed.getId());
        host.setName(embed.getName());
        return host;
    }

    private List<GameSessionReadModel.SessionPlayer> toPlayers(List<SessionPlayerEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toPlayer).collect(Collectors.toList());
    }

    private GameSessionReadModel.SessionPlayer toPlayer(SessionPlayerEmbed embed) {
        GameSessionReadModel.SessionPlayer player = new GameSessionReadModel.SessionPlayer();
        player.setId(embed.getId());
        player.setUserId(embed.getUserId());
        player.setNickname(embed.getNickname());
        player.setScore(embed.getScore());
        player.setConnected(embed.isConnected());
        player.setJoinedAt(embed.getJoinedAt());
        player.setLeftAt(embed.getLeftAt());
        return player;
    }

    private List<GameSessionReadModel.SessionQuestion> toQuestions(List<SessionQuestionEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toQuestion).collect(Collectors.toList());
    }

    private GameSessionReadModel.SessionQuestion toQuestion(SessionQuestionEmbed embed) {
        GameSessionReadModel.SessionQuestion question = new GameSessionReadModel.SessionQuestion();
        question.setId(embed.getId());
        question.setSourceQuestionId(embed.getSourceQuestionId());
        question.setOrderIndex(embed.getOrderIndex());
        question.setPoints(embed.getPoints());
        question.setTimeLimitSeconds(embed.getTimeLimitSeconds());
        question.setTitle(embed.getTitle());
        question.setDescription(embed.getDescription());
        question.setQuestionType(embed.getQuestionType());
        question.setOpenedAt(embed.getOpenedAt());
        question.setClosedAt(embed.getClosedAt());
        question.setAnswerOptions(toAnswerOptions(embed.getAnswerOptions()));
        return question;
    }

    private List<GameSessionReadModel.SessionAnswerOption> toAnswerOptions(
            List<SessionAnswerOptionEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toAnswerOption).collect(Collectors.toList());
    }

    private GameSessionReadModel.SessionAnswerOption toAnswerOption(SessionAnswerOptionEmbed embed) {
        GameSessionReadModel.SessionAnswerOption option = new GameSessionReadModel.SessionAnswerOption();
        option.setId(embed.getId());
        option.setSourceAnswerOptionId(embed.getSourceAnswerOptionId());
        option.setText(embed.getText());
        option.setCorrect(embed.isCorrect());
        option.setOrderIndex(embed.getOrderIndex());
        return option;
    }

    private List<GameSessionReadModel.PlayerAnswer> toPlayerAnswers(List<PlayerAnswerEmbed> embeds) {
        if (embeds == null || embeds.isEmpty()) {
            return Collections.emptyList();
        }

        return embeds.stream().map(this::toPlayerAnswer).collect(Collectors.toList());
    }

    private GameSessionReadModel.PlayerAnswer toPlayerAnswer(PlayerAnswerEmbed embed) {
        GameSessionReadModel.PlayerAnswer answer = new GameSessionReadModel.PlayerAnswer();
        answer.setId(embed.getId());
        answer.setSessionQuestionId(embed.getSessionQuestionId());
        answer.setSessionPlayerId(embed.getSessionPlayerId());
        answer.setSessionAnswerOptionId(embed.getSessionAnswerOptionId());
        answer.setCorrect(embed.isCorrect());
        answer.setResponseTimeMs(embed.getResponseTimeMs());
        answer.setAwardedPoints(embed.getAwardedPoints());
        answer.setAnsweredAt(embed.getAnsweredAt());
        return answer;
    }
}
