package kahoot.clabs.quiz.infrastructure.rest;

import java.util.List;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kahoot.clabs.quiz.application.command.CreateCategoryCommand;
import kahoot.clabs.quiz.application.command.UpdateCategoryCommand;
import kahoot.clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.quiz.application.usecase.CreateCategoryUseCase;
import kahoot.clabs.quiz.application.usecase.DeleteCategoryUseCase;
import kahoot.clabs.quiz.application.usecase.GetCategoryUseCase;
import kahoot.clabs.quiz.application.usecase.ListCategoriesUseCase;
import kahoot.clabs.quiz.application.usecase.UpdateCategoryUseCase;

@Path("/api/v1/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    @Inject
    public CategoryResource(
            CreateCategoryUseCase createCategoryUseCase,
            GetCategoryUseCase getCategoryUseCase,
            ListCategoriesUseCase listCategoriesUseCase,
            UpdateCategoryUseCase updateCategoryUseCase,
            DeleteCategoryUseCase deleteCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.getCategoryUseCase = getCategoryUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    @POST
    public Response create(@Valid CreateCategoryCommand command) {
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(
                        Response.Status.CREATED.getStatusCode(),
                        "Category created",
                        createCategoryUseCase.execute(command)))
                .build();
    }

    @GET
    @Path("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathParam("id") UUID id) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(), "Category retrieved", getCategoryUseCase.execute(id));
    }

    @GET
    public ApiResponse<List<CategoryResponse>> listByOrganization(@QueryParam("organizationId") UUID organizationId) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Categories retrieved",
                listCategoriesUseCase.execute(organizationId));
    }

    @PUT
    @Path("/{id}")
    public ApiResponse<CategoryResponse> update(
            @PathParam("id") UUID id, @Valid UpdateCategoryCommand command) {
        return ApiResponse.success(
                Response.Status.OK.getStatusCode(),
                "Category updated",
                updateCategoryUseCase.execute(id, command));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        deleteCategoryUseCase.execute(id);
        return Response.noContent().build();
    }
}
