package kahoot.clabs.infrastructure.rest;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kahoot.clabs.application.command.AssignRoleCommand;
import kahoot.clabs.application.command.ChangePasswordCommand;
import kahoot.clabs.application.command.UpdateProfileCommand;
import kahoot.clabs.application.dto.UserProfileResponse;
import kahoot.clabs.application.dto.UserRoleResponse;
import kahoot.clabs.application.query.GetUserProfileQuery;
import kahoot.clabs.application.query.GetUserRolesQuery;
import kahoot.clabs.application.usecase.AssignRoleUseCase;
import kahoot.clabs.application.usecase.ChangePasswordUseCase;
import kahoot.clabs.application.usecase.GetUserProfileUseCase;
import kahoot.clabs.application.usecase.GetUserRolesUseCase;
import kahoot.clabs.application.usecase.UpdateProfileUseCase;
import kahoot.clabs.infrastructure.web.ApiResponse;

@Tag(name = "Users")
@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final GetUserRolesUseCase getUserRolesUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final AssignRoleUseCase assignRoleUseCase;

    public UserResource(
            GetUserProfileUseCase getUserProfileUseCase,
            GetUserRolesUseCase getUserRolesUseCase,
            UpdateProfileUseCase updateProfileUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            AssignRoleUseCase assignRoleUseCase) {
        this.getUserProfileUseCase = getUserProfileUseCase;
        this.getUserRolesUseCase = getUserRolesUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        UserProfileResponse response = getUserProfileUseCase.execute(new GetUserProfileQuery(id));
        return Response.ok(ApiResponse.success(Response.Status.OK.getStatusCode(), "User retrieved", response))
                .build();
    }

    @PUT
    @Path("/{id}/profile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateProfile(@PathParam("id") UUID id, @BeanParam UpdateProfileForm form) throws IOException {
        UpdateProfileCommand command = new UpdateProfileCommand(
                form.phoneNumber,
                parseBirthDate(form.birthDate),
                form.bio,
                form.location);

        byte[] content = null;
        String contentType = null;
        String filename = null;
        if (form.avatar != null && form.avatar.filePath() != null) {
            content = java.nio.file.Files.readAllBytes(form.avatar.filePath());
            contentType = form.avatar.contentType();
            filename = form.avatar.fileName();
        }

        UserProfileResponse response = updateProfileUseCase.execute(id, command, content, contentType, filename);
        return Response.ok(
                ApiResponse.success(Response.Status.OK.getStatusCode(), "User profile updated", response))
                .build();
    }

    @PUT
    @Path("/{id}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(@PathParam("id") UUID id, @Valid ChangePasswordCommand command) {
        changePasswordUseCase.execute(id, command);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/roles")
    public Response getRoles(@PathParam("id") UUID id) {
        List<UserRoleResponse> roles = getUserRolesUseCase.execute(new GetUserRolesQuery(id));
        return Response.ok(
                ApiResponse.success(Response.Status.OK.getStatusCode(), "User roles retrieved", roles))
                .build();
    }

    @PUT
    @Path("/{id}/role")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response assignRole(@PathParam("id") UUID id, @Valid AssignRoleCommand command) {
        UserProfileResponse response = assignRoleUseCase.execute(id, command);
        return Response.ok(
                ApiResponse.success(Response.Status.OK.getStatusCode(), "User role assigned", response))
                .build();
    }

    private LocalDate parseBirthDate(String birthDate) {
        if (birthDate == null || birthDate.isBlank()) {
            return null;
        }
        return LocalDate.parse(birthDate);
    }

    public static class UpdateProfileForm {

        @RestForm
        public String phoneNumber;

        @RestForm
        public String birthDate;

        @RestForm
        public String bio;

        @RestForm
        public String location;

        @RestForm("avatar")
        public FileUpload avatar;
    }
}
