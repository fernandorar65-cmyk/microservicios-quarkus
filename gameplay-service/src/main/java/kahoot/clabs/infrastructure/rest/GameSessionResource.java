package kahoot.clabs.infrastructure.rest;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import kahoot.clabs.application.command.CreateGameSessionCommand;
import kahoot.clabs.application.command.HostActionCommand;
import kahoot.clabs.application.command.JoinSessionCommand;
import kahoot.clabs.application.command.LeaveSessionCommand;
import kahoot.clabs.application.command.OpenQuestionCommand;
import kahoot.clabs.application.command.SubmitAnswerCommand;
import kahoot.clabs.application.command.UpdateNicknameCommand;
import kahoot.clabs.application.dto.GameSessionResponse;
import kahoot.clabs.application.dto.LeaderboardEntryResponse;
import kahoot.clabs.application.dto.PlayerAnswerResponse;
import kahoot.clabs.application.dto.QuestionResultResponse;
import kahoot.clabs.application.dto.SessionPlayerResponse;
import kahoot.clabs.application.dto.SessionQuestionResponse;
import kahoot.clabs.application.query.GetCurrentSessionQuestionQuery;
import kahoot.clabs.application.query.GetGameSessionQuery;
import kahoot.clabs.application.query.GetLeaderboardQuery;
import kahoot.clabs.application.query.GetMyAnswersQuery;
import kahoot.clabs.application.query.GetSessionQuestionResultQuery;
import kahoot.clabs.application.query.ListGameSessionsQuery;
import kahoot.clabs.application.query.ListSessionPlayersQuery;
import kahoot.clabs.application.query.ListSessionQuestionsQuery;
import kahoot.clabs.application.usecase.CreateGameSessionUseCase;
import kahoot.clabs.application.usecase.GetGameSessionUseCase;
import kahoot.clabs.application.usecase.GetLeaderboardUseCase;
import kahoot.clabs.application.usecase.GetMyAnswersUseCase;
import kahoot.clabs.application.usecase.GetSessionQuestionsUseCase;
import kahoot.clabs.application.usecase.ListGameSessionsUseCase;
import kahoot.clabs.application.usecase.ListSessionPlayersUseCase;
import kahoot.clabs.application.usecase.ManageSessionLifecycleUseCase;
import kahoot.clabs.application.usecase.ManageSessionPlayersUseCase;
import kahoot.clabs.application.usecase.ManageSessionQuestionsUseCase;
import kahoot.clabs.application.usecase.SubmitAnswerUseCase;

@Tag(name = "Sessions")
@Path("/api/v1/organizations/{organizationId}/sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameSessionResource {

    @Inject
    CreateGameSessionUseCase createGameSessionUseCase;

    @Inject
    GetGameSessionUseCase getGameSessionUseCase;

    @Inject
    ListGameSessionsUseCase listGameSessionsUseCase;

    @Inject
    ManageSessionLifecycleUseCase manageSessionLifecycleUseCase;

    @Inject
    ManageSessionPlayersUseCase manageSessionPlayersUseCase;

    @Inject
    ListSessionPlayersUseCase listSessionPlayersUseCase;

    @Inject
    ManageSessionQuestionsUseCase manageSessionQuestionsUseCase;

    @Inject
    GetSessionQuestionsUseCase getSessionQuestionsUseCase;

    @Inject
    SubmitAnswerUseCase submitAnswerUseCase;

    @Inject
    GetMyAnswersUseCase getMyAnswersUseCase;

    @Inject
    GetLeaderboardUseCase getLeaderboardUseCase;

    @POST
    public Response create(
            @PathParam("organizationId") UUID organizationId,
            @Valid CreateGameSessionCommand command) {
        GameSessionResponse response = createGameSessionUseCase.execute(organizationId, command);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(201, "Game session created", response))
                .build();
    }

    @GET
    public Response list(
            @PathParam("organizationId") UUID organizationId,
            @QueryParam("status") String status,
            @QueryParam("quizId") UUID quizId) {
        List<GameSessionResponse> sessions = listGameSessionsUseCase.execute(
                new ListGameSessionsQuery(organizationId, status, quizId));
        return Response.ok(ApiResponse.success(200, "Game sessions listed", sessions)).build();
    }

    @GET
    @Path("/{sessionId}")
    public Response get(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId) {
        GameSessionResponse response = getGameSessionUseCase.execute(
                new GetGameSessionQuery(organizationId, sessionId));
        return Response.ok(ApiResponse.success(200, "Game session retrieved", response)).build();
    }

    @POST
    @Path("/{sessionId}/start")
    public Response start(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @Valid HostActionCommand command) {
        GameSessionResponse response = manageSessionLifecycleUseCase.start(organizationId, sessionId, command);
        return Response.ok(ApiResponse.success(200, "Game session started", response)).build();
    }

    @POST
    @Path("/{sessionId}/cancel")
    public Response cancel(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @Valid HostActionCommand command) {
        GameSessionResponse response = manageSessionLifecycleUseCase.cancel(organizationId, sessionId, command);
        return Response.ok(ApiResponse.success(200, "Game session cancelled", response)).build();
    }

    @POST
    @Path("/{sessionId}/finish")
    public Response finish(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @Valid HostActionCommand command) {
        GameSessionResponse response = manageSessionLifecycleUseCase.finish(organizationId, sessionId, command);
        return Response.ok(ApiResponse.success(200, "Game session finished", response)).build();
    }

    @POST
    @Path("/{sessionId}/join")
    public Response join(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @Valid JoinSessionCommand command) {
        GameSessionResponse response = manageSessionPlayersUseCase.join(organizationId, sessionId, command);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(201, "Joined game session", response))
                .build();
    }

    @POST
    @Path("/{sessionId}/leave")
    public Response leave(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @Valid LeaveSessionCommand command) {
        GameSessionResponse response = manageSessionPlayersUseCase.leave(organizationId, sessionId, command);
        return Response.ok(ApiResponse.success(200, "Left game session", response)).build();
    }

    @GET
    @Path("/{sessionId}/players")
    public Response players(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId) {
        List<SessionPlayerResponse> players = listSessionPlayersUseCase.execute(
                new ListSessionPlayersQuery(organizationId, sessionId));
        return Response.ok(ApiResponse.success(200, "Session players listed", players)).build();
    }

    @PATCH
    @Path("/{sessionId}/players/me")
    public Response updateNickname(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @Valid UpdateNicknameCommand command) {
        SessionPlayerResponse response = manageSessionPlayersUseCase.updateNickname(
                organizationId, sessionId, command);
        return Response.ok(ApiResponse.success(200, "Nickname updated", response)).build();
    }

    @POST
    @Path("/{sessionId}/questions/open")
    public Response openQuestion(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @Valid OpenQuestionCommand command) {
        GameSessionResponse response = manageSessionQuestionsUseCase.open(organizationId, sessionId, command);
        return Response.ok(ApiResponse.success(200, "Question opened", response)).build();
    }

    @POST
    @Path("/{sessionId}/questions/close")
    public Response closeQuestion(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @Valid HostActionCommand command) {
        GameSessionResponse response = manageSessionQuestionsUseCase.close(organizationId, sessionId, command);
        return Response.ok(ApiResponse.success(200, "Question closed", response)).build();
    }

    @POST
    @Path("/{sessionId}/questions/next")
    public Response nextQuestion(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @Valid HostActionCommand command) {
        GameSessionResponse response = manageSessionQuestionsUseCase.next(organizationId, sessionId, command);
        return Response.ok(ApiResponse.success(200, "Advanced to next question", response)).build();
    }

    @GET
    @Path("/{sessionId}/questions")
    public Response listQuestions(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @QueryParam("asHost") @DefaultValue("false") boolean asHost) {
        List<SessionQuestionResponse> questions = getSessionQuestionsUseCase.list(
                new ListSessionQuestionsQuery(organizationId, sessionId, asHost));
        return Response.ok(ApiResponse.success(200, "Session questions listed", questions)).build();
    }

    @GET
    @Path("/{sessionId}/questions/current")
    public Response currentQuestion(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId) {
        SessionQuestionResponse response = getSessionQuestionsUseCase.current(
                new GetCurrentSessionQuestionQuery(organizationId, sessionId));
        return Response.ok(ApiResponse.success(200, "Current question retrieved", response)).build();
    }

    @GET
    @Path("/{sessionId}/questions/{sessionQuestionId}/result")
    public Response questionResult(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @PathParam("sessionQuestionId") UUID sessionQuestionId) {
        QuestionResultResponse response = getSessionQuestionsUseCase.result(
                new GetSessionQuestionResultQuery(organizationId, sessionId, sessionQuestionId));
        return Response.ok(ApiResponse.success(200, "Question result retrieved", response)).build();
    }

    @POST
    @Path("/{sessionId}/answers")
    public Response submitAnswer(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @Valid SubmitAnswerCommand command) {
        PlayerAnswerResponse response = submitAnswerUseCase.execute(organizationId, sessionId, command);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(201, "Answer submitted", response))
                .build();
    }

    @GET
    @Path("/{sessionId}/answers/me")
    public Response myAnswers(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId,
            @QueryParam("userId") UUID userId) {
        List<PlayerAnswerResponse> answers = getMyAnswersUseCase.execute(
                new GetMyAnswersQuery(organizationId, sessionId, userId));
        return Response.ok(ApiResponse.success(200, "Player answers retrieved", answers)).build();
    }

    @GET
    @Path("/{sessionId}/leaderboard")
    public Response leaderboard(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("sessionId") UUID sessionId) {
        List<LeaderboardEntryResponse> entries = getLeaderboardUseCase.execute(
                new GetLeaderboardQuery(organizationId, sessionId));
        return Response.ok(ApiResponse.success(200, "Leaderboard retrieved", entries)).build();
    }
}
