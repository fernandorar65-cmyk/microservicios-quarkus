package kahoot.clabs.quiz.infrastructure.rest;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kahoot.clabs.quiz.application.command.AnswerOptionCommand;
import kahoot.clabs.quiz.application.command.CreateQuizCommand;
import kahoot.clabs.quiz.application.command.DuplicateQuizCommand;
import kahoot.clabs.quiz.application.command.QuestionAssetCommand;
import kahoot.clabs.quiz.application.command.QuestionCommand;
import kahoot.clabs.quiz.application.command.ReorderAnswerOptionsCommand;
import kahoot.clabs.quiz.application.command.ReorderQuestionsCommand;
import kahoot.clabs.quiz.application.command.UpdateQuestionCommand;
import kahoot.clabs.quiz.application.command.UpdateQuizCommand;
import kahoot.clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.quiz.application.query.GetQuizQuery;
import kahoot.clabs.quiz.application.query.ListQuizzesQuery;
import kahoot.clabs.quiz.application.usecase.CreateQuizUseCase;
import kahoot.clabs.quiz.application.usecase.EditQuizContentUseCase;
import kahoot.clabs.quiz.application.usecase.GetQuizUseCase;
import kahoot.clabs.quiz.application.usecase.ListQuizzesUseCase;
import kahoot.clabs.quiz.application.usecase.ManageQuizCategoriesUseCase;
import kahoot.clabs.quiz.application.usecase.ManageQuizLifecycleUseCase;
import kahoot.clabs.quiz.application.usecase.ManageQuizQuestionsUseCase;
import kahoot.clabs.quiz.application.usecase.UpdateQuizUseCase;
import kahoot.clabs.quiz.application.usecase.UploadQuizImageUseCase;

@Tag(name = "Quizzes")
@Path("/api/v1/organizations/{organizationId}/quizzes")
@Produces(MediaType.APPLICATION_JSON)
public class QuizResource {

    private final CreateQuizUseCase createQuizUseCase;
    private final GetQuizUseCase getQuizUseCase;
    private final ListQuizzesUseCase listQuizzesUseCase;
    private final EditQuizContentUseCase editQuizContentUseCase;
    private final UpdateQuizUseCase updateQuizUseCase;
    private final ManageQuizCategoriesUseCase manageQuizCategoriesUseCase;
    private final ManageQuizQuestionsUseCase manageQuizQuestionsUseCase;
    private final ManageQuizLifecycleUseCase manageQuizLifecycleUseCase;
    private final UploadQuizImageUseCase uploadQuizImageUseCase;

    @Inject
    public QuizResource(
            CreateQuizUseCase createQuizUseCase,
            GetQuizUseCase getQuizUseCase,
            ListQuizzesUseCase listQuizzesUseCase,
            EditQuizContentUseCase editQuizContentUseCase,
            UpdateQuizUseCase updateQuizUseCase,
            ManageQuizCategoriesUseCase manageQuizCategoriesUseCase,
            ManageQuizQuestionsUseCase manageQuizQuestionsUseCase,
            ManageQuizLifecycleUseCase manageQuizLifecycleUseCase,
            UploadQuizImageUseCase uploadQuizImageUseCase) {
        this.createQuizUseCase = createQuizUseCase;
        this.getQuizUseCase = getQuizUseCase;
        this.listQuizzesUseCase = listQuizzesUseCase;
        this.editQuizContentUseCase = editQuizContentUseCase;
        this.updateQuizUseCase = updateQuizUseCase;
        this.manageQuizCategoriesUseCase = manageQuizCategoriesUseCase;
        this.manageQuizQuestionsUseCase = manageQuizQuestionsUseCase;
        this.manageQuizLifecycleUseCase = manageQuizLifecycleUseCase;
        this.uploadQuizImageUseCase = uploadQuizImageUseCase;
    }

    @POST
    public Response create(@PathParam("organizationId") UUID organizationId, @Valid CreateQuizCommand command) {
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(
                        Response.Status.CREATED.getStatusCode(),
                        "Quiz created",
                        createQuizUseCase.execute(organizationId, command)))
                .build();
    }

    @GET
    @Path("/{quizId}")
    public ApiResponse<QuizResponse> getById(
            @PathParam("organizationId") UUID organizationId, @PathParam("quizId") UUID quizId) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Quiz retrieved",
                getQuizUseCase.execute(new GetQuizQuery(organizationId, quizId)));
    }

    @PUT
    @Path("/{quizId}")
    public ApiResponse<QuizResponse> update(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @Valid UpdateQuizCommand command) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Quiz updated",
                updateQuizUseCase.execute(organizationId, quizId, command));
    }

    @GET
    public ApiResponse<List<QuizResponse>> list(@PathParam("organizationId") UUID organizationId) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Quizzes retrieved",
                listQuizzesUseCase.execute(new ListQuizzesQuery(organizationId)));
    }

    @POST
    @Path("/{quizId}/categories/{categoryId}")
    public ApiResponse<QuizResponse> assignCategory(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("categoryId") UUID categoryId) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Category assigned to quiz",
                manageQuizCategoriesUseCase.assign(organizationId, quizId, categoryId));
    }

    @DELETE
    @Path("/{quizId}/categories/{categoryId}")
    public Response removeCategory(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("categoryId") UUID categoryId) {
        manageQuizCategoriesUseCase.remove(organizationId, quizId, categoryId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{quizId}/questions")
    public Response addQuestion(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @Valid QuestionCommand command) {
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(
                        Response.Status.CREATED.getStatusCode(),
                        "Question added",
                        manageQuizQuestionsUseCase.add(organizationId, quizId, command)))
                .build();
    }

    @PUT
    @Path("/{quizId}/questions/{questionId}")
    public ApiResponse<QuizResponse> updateQuestion(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("questionId") UUID questionId,
            @Valid UpdateQuestionCommand command) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Question updated",
                manageQuizQuestionsUseCase.update(organizationId, quizId, questionId, command));
    }

    @PUT
    @Path("/{quizId}/questions/order")
    public ApiResponse<QuizResponse> reorderQuestions(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @Valid ReorderQuestionsCommand command) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Questions reordered",
                manageQuizQuestionsUseCase.reorder(organizationId, quizId, command));
    }

    @DELETE
    @Path("/{quizId}/questions/{questionId}")
    public Response removeQuestion(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("questionId") UUID questionId) {
        manageQuizQuestionsUseCase.remove(organizationId, quizId, questionId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{quizId}/publish")
    public ApiResponse<QuizResponse> publish(
            @PathParam("organizationId") UUID organizationId, @PathParam("quizId") UUID quizId) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Quiz published",
                manageQuizLifecycleUseCase.publish(organizationId, quizId));
    }

    @POST
    @Path("/{quizId}/archive")
    public ApiResponse<QuizResponse> archive(
            @PathParam("organizationId") UUID organizationId, @PathParam("quizId") UUID quizId) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Quiz archived",
                manageQuizLifecycleUseCase.archive(organizationId, quizId));
    }

    @POST
    @Path("/{quizId}/duplicate")
    public Response duplicate(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @Valid DuplicateQuizCommand command) {
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(
                        Response.Status.CREATED.getStatusCode(),
                        "Quiz duplicated",
                        manageQuizLifecycleUseCase.duplicate(organizationId, quizId, command)))
                .build();
    }

    @POST
    @Path("/{quizId}/questions/{questionId}/options")
    public Response addAnswerOption(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("questionId") UUID questionId,
            @Valid AnswerOptionCommand command) {
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(
                        Response.Status.CREATED.getStatusCode(),
                        "Answer option added",
                        editQuizContentUseCase.addAnswerOption(organizationId, quizId, questionId, command)))
                .build();
    }

    @PUT
    @Path("/{quizId}/questions/{questionId}/options/{optionId}")
    public ApiResponse<QuizResponse> updateAnswerOption(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("questionId") UUID questionId,
            @PathParam("optionId") UUID optionId,
            @Valid AnswerOptionCommand command) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Answer option updated",
                editQuizContentUseCase.updateAnswerOption(
                        organizationId, quizId, questionId, optionId, command));
    }

    @PUT
    @Path("/{quizId}/questions/{questionId}/options/order")
    public ApiResponse<QuizResponse> reorderAnswerOptions(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("questionId") UUID questionId,
            @Valid ReorderAnswerOptionsCommand command) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Answer options reordered",
                editQuizContentUseCase.reorderAnswerOptions(organizationId, quizId, questionId, command));
    }

    @DELETE
    @Path("/{quizId}/questions/{questionId}/options/{optionId}")
    public Response removeAnswerOption(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("questionId") UUID questionId,
            @PathParam("optionId") UUID optionId) {
        editQuizContentUseCase.removeAnswerOption(organizationId, quizId, questionId, optionId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{quizId}/questions/{questionId}/assets")
    public Response addAsset(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("questionId") UUID questionId,
            @Valid QuestionAssetCommand command) {
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(
                        Response.Status.CREATED.getStatusCode(),
                        "Question asset added",
                        editQuizContentUseCase.addAsset(organizationId, quizId, questionId, command)))
                .build();
    }

    @PUT
    @Path("/{quizId}/questions/{questionId}/assets/{assetId}")
    public ApiResponse<QuizResponse> updateAsset(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("questionId") UUID questionId,
            @PathParam("assetId") UUID assetId,
            @Valid QuestionAssetCommand command) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Question asset updated",
                editQuizContentUseCase.updateAsset(organizationId, quizId, questionId, assetId, command));
    }

    @DELETE
    @Path("/{quizId}/questions/{questionId}/assets/{assetId}")
    public Response removeAsset(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("questionId") UUID questionId,
            @PathParam("assetId") UUID assetId) {
        editQuizContentUseCase.removeAsset(organizationId, quizId, questionId, assetId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{quizId}/questions/{questionId}/assets/images")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImage(
            @PathParam("organizationId") UUID organizationId,
            @PathParam("quizId") UUID quizId,
            @PathParam("questionId") UUID questionId,
            @RestForm("file") FileUpload file,
            @RestForm("altText") String altText)
            throws IOException {
        byte[] content = Files.readAllBytes(file.uploadedFile());
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(
                        Response.Status.CREATED.getStatusCode(),
                        "Image uploaded",
                        uploadQuizImageUseCase.execute(
                                organizationId,
                                quizId,
                                questionId,
                                content,
                                file.contentType(),
                                file.fileName(),
                                altText)))
                .build();
    }
}
