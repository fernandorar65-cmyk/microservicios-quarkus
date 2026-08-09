package kahoot.clabs.infrastructure.rest;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.io.IOException;
import java.util.UUID;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestForm;

import kahoot.clabs.application.command.CreateOrganizationCommand;
import kahoot.clabs.application.command.InviteMemberCommand;
import kahoot.clabs.application.command.UpdateOrganizationCommand;
import kahoot.clabs.application.dto.OrganizationResponse;
import kahoot.clabs.application.query.GetOrganizationQuery;
import kahoot.clabs.application.usecase.CreateOrganizationUseCase;
import kahoot.clabs.application.usecase.GetOrganizationUseCase;
import kahoot.clabs.application.usecase.InviteMemberUseCase;
import kahoot.clabs.application.usecase.UpdateOrganizationUseCase;

@Tag(name = "Organizations")
@Path("/api/v1/organizations")
@Produces(MediaType.APPLICATION_JSON)
public class OrganizationResource {

    @Inject
    CreateOrganizationUseCase createOrganizationUseCase;

    @Inject
    UpdateOrganizationUseCase updateOrganizationUseCase;

    @Inject
    GetOrganizationUseCase getOrganizationUseCase;

    @Inject
    InviteMemberUseCase inviteMemberUseCase;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response create(
            @RestForm String name,
            @RestForm String slug,
            @RestForm String description,
            @RestForm("logo") FileUpload logo) throws IOException {
        CreateOrganizationCommand command = new CreateOrganizationCommand(name, slug, description);
        byte[] content = null;
        String contentType = null;
        String filename = null;
        if (logo != null && logo.size() > 0) {
            content = java.nio.file.Files.readAllBytes(logo.uploadedFile());
            contentType = logo.contentType();
            filename = logo.fileName();
        }

        OrganizationResponse response = createOrganizationUseCase.execute(command, content, contentType, filename);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(201, "Organization created", response))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        OrganizationResponse response = getOrganizationUseCase.execute(new GetOrganizationQuery(id));
        return Response.ok(ApiResponse.success(200, "Organization retrieved", response)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, @Valid UpdateOrganizationCommand command) {
        OrganizationResponse response = updateOrganizationUseCase.execute(id, command);
        return Response.ok(ApiResponse.success(200, "Organization updated", response)).build();
    }

    @POST
    @Path("/{id}/invitations")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response inviteMember(@PathParam("id") UUID id, @Valid InviteMemberCommand command) {
        OrganizationResponse response = inviteMemberUseCase.execute(id, command);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(201, "Member invited", response))
                .build();
    }
}
