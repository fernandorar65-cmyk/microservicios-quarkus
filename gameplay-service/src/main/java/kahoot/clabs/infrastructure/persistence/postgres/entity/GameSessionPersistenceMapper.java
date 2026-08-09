package kahoot.clabs.infrastructure.persistence.postgres.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.entity.PlayerAnswer;
import kahoot.clabs.domain.entity.SessionAnswerOption;
import kahoot.clabs.domain.entity.SessionPlayer;
import kahoot.clabs.domain.entity.SessionQuestion;
import kahoot.clabs.domain.valueobject.SessionStatus;

public final class GameSessionPersistenceMapper {

    private GameSessionPersistenceMapper() {
    }

    public static GameSessionJpaEntity toEntity(GameSession session) {
        GameSessionJpaEntity entity = new GameSessionJpaEntity();
        entity.setId(session.getId());
        entity.setOrganizationId(session.getOrganizationId());
        entity.setQuizId(session.getQuizId());
        entity.setHostUserId(session.getHostUserId());
        entity.setStatus(session.getStatus().name());
        entity.setCurrentQuestionIndex(session.getCurrentQuestionIndex());
        entity.setStartedAt(session.getStartedAt());
        entity.setFinishedAt(session.getFinishedAt());
        entity.setCreatedAt(session.getCreatedAt());
        entity.setUpdatedAt(session.getUpdatedAt());

        Set<SessionPlayerJpaEntity> players = new HashSet<>();
        for (SessionPlayer player : session.getPlayers()) {
            SessionPlayerJpaEntity playerEntity = toEntity(player);
            playerEntity.setSession(entity);
            players.add(playerEntity);
        }
        entity.setPlayers(new ArrayList<>(players));

        List<SessionQuestionJpaEntity> questions = new ArrayList<>();
        for (SessionQuestion question : session.getQuestions()) {
            SessionQuestionJpaEntity questionEntity = toEntity(question);
            questionEntity.setSession(entity);
            questions.add(questionEntity);
        }
        entity.setQuestions(questions);
        return entity;
    }

    public static GameSession toDomain(
            GameSessionJpaEntity entity,
            List<PlayerAnswerJpaEntity> answerEntities) {
        List<SessionPlayer> players = entity.getPlayers().stream()
                .map(GameSessionPersistenceMapper::toDomain)
                .toList();
        List<SessionQuestion> questions = entity.getQuestions().stream()
                .map(GameSessionPersistenceMapper::toDomain)
                .toList();
        List<PlayerAnswer> answers = answerEntities.stream()
                .map(GameSessionPersistenceMapper::toDomain)
                .toList();
        return GameSession.rehydrate(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getQuizId(),
                entity.getHostUserId(),
                SessionStatus.valueOf(entity.getStatus()),
                entity.getCurrentQuestionIndex(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                players,
                questions,
                answers,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static List<PlayerAnswerJpaEntity> toAnswerEntities(
            GameSession session,
            GameSessionJpaEntity savedEntity) {
        Map<UUID, SessionPlayerJpaEntity> playersById = savedEntity.getPlayers().stream()
                .collect(Collectors.toMap(SessionPlayerJpaEntity::getId, Function.identity()));
        Map<UUID, SessionQuestionJpaEntity> questionsById = savedEntity.getQuestions().stream()
                .collect(Collectors.toMap(SessionQuestionJpaEntity::getId, Function.identity()));
        Map<UUID, SessionAnswerOptionJpaEntity> optionsById = savedEntity.getQuestions().stream()
                .flatMap(question -> question.getAnswerOptions().stream())
                .collect(Collectors.toMap(SessionAnswerOptionJpaEntity::getId, Function.identity()));

        return session.getAnswers().stream()
                .map(answer -> toEntity(answer, playersById, questionsById, optionsById))
                .toList();
    }

    private static SessionPlayerJpaEntity toEntity(SessionPlayer player) {
        SessionPlayerJpaEntity entity = new SessionPlayerJpaEntity();
        entity.setId(player.getId());
        entity.setUserId(player.getUserId());
        entity.setNickname(player.getNickname().value());
        entity.setScore(player.getScore());
        entity.setConnected(player.isConnected());
        entity.setJoinedAt(player.getJoinedAt());
        entity.setLeftAt(player.getLeftAt());
        return entity;
    }

    private static SessionPlayer toDomain(SessionPlayerJpaEntity entity) {
        return SessionPlayer.rehydrate(
                entity.getId(),
                entity.getSession().getId(),
                entity.getUserId(),
                entity.getNickname(),
                entity.getScore(),
                entity.isConnected(),
                entity.getJoinedAt(),
                entity.getLeftAt());
    }

    private static SessionQuestionJpaEntity toEntity(SessionQuestion question) {
        SessionQuestionJpaEntity entity = new SessionQuestionJpaEntity();
        entity.setId(question.getId());
        entity.setSourceQuestionId(question.getSourceQuestionId());
        entity.setOrderIndex(question.getOrderIndex());
        entity.setPoints(question.getPoints());
        entity.setTimeLimitSeconds(question.getTimeLimitSeconds());
        entity.setTitle(question.getTitle());
        entity.setDescription(question.getDescription());
        entity.setQuestionType(question.getQuestionType());
        entity.setOpenedAt(question.getOpenedAt());
        entity.setClosedAt(question.getClosedAt());

        List<SessionAnswerOptionJpaEntity> options = new ArrayList<>();
        for (SessionAnswerOption option : question.getOptions()) {
            SessionAnswerOptionJpaEntity optionEntity = toEntity(option);
            optionEntity.setSessionQuestion(entity);
            options.add(optionEntity);
        }
        entity.setAnswerOptions(options);
        return entity;
    }

    private static SessionQuestion toDomain(SessionQuestionJpaEntity entity) {
        List<SessionAnswerOption> options = entity.getAnswerOptions().stream()
                .map(GameSessionPersistenceMapper::toDomain)
                .toList();
        return SessionQuestion.rehydrate(
                entity.getId(),
                entity.getSession().getId(),
                entity.getSourceQuestionId(),
                entity.getOrderIndex(),
                entity.getPoints(),
                entity.getTimeLimitSeconds(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getQuestionType(),
                entity.getOpenedAt(),
                entity.getClosedAt(),
                options);
    }

    private static SessionAnswerOptionJpaEntity toEntity(SessionAnswerOption option) {
        SessionAnswerOptionJpaEntity entity = new SessionAnswerOptionJpaEntity();
        entity.setId(option.getId());
        entity.setSourceAnswerOptionId(option.getSourceAnswerOptionId());
        entity.setText(option.getText());
        entity.setCorrect(option.isCorrect());
        entity.setOrderIndex(option.getOrderIndex());
        return entity;
    }

    private static SessionAnswerOption toDomain(SessionAnswerOptionJpaEntity entity) {
        return SessionAnswerOption.rehydrate(
                entity.getId(),
                entity.getSessionQuestion().getId(),
                entity.getSourceAnswerOptionId(),
                entity.getText(),
                entity.isCorrect(),
                entity.getOrderIndex());
    }

    private static PlayerAnswerJpaEntity toEntity(
            PlayerAnswer answer,
            Map<UUID, SessionPlayerJpaEntity> playersById,
            Map<UUID, SessionQuestionJpaEntity> questionsById,
            Map<UUID, SessionAnswerOptionJpaEntity> optionsById) {
        PlayerAnswerJpaEntity entity = new PlayerAnswerJpaEntity();
        entity.setId(answer.getId());
        entity.setSessionPlayer(playersById.get(answer.getSessionPlayerId()));
        entity.setSessionQuestion(questionsById.get(answer.getSessionQuestionId()));
        UUID optionId = answer.getSessionAnswerOptionId();
        if (optionId != null) {
            entity.setSessionAnswerOption(optionsById.get(optionId));
        }
        entity.setCorrect(answer.isCorrect());
        entity.setResponseTimeMs(answer.getResponseTimeMs());
        entity.setAwardedPoints(answer.getAwardedPoints());
        entity.setAnsweredAt(answer.getAnsweredAt());
        return entity;
    }

    private static PlayerAnswer toDomain(PlayerAnswerJpaEntity entity) {
        return PlayerAnswer.rehydrate(
                entity.getId(),
                entity.getSessionQuestion().getId(),
                entity.getSessionPlayer().getId(),
                entity.getSessionAnswerOption() != null ? entity.getSessionAnswerOption().getId() : null,
                entity.isCorrect(),
                entity.getResponseTimeMs(),
                entity.getAwardedPoints(),
                entity.getAnsweredAt());
    }
}
