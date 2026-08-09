package kahoot.clabs.infrastructure.rest;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kahoot.clabs.application.command.LoginCommand;
import kahoot.clabs.application.command.RegisterUserCommand;
import kahoot.clabs.application.dto.AuthUserResponse;
import kahoot.clabs.application.usecase.LoginUserUseCase;
import kahoot.clabs.application.usecase.RegisterUserUseCase;
import kahoot.clabs.infrastructure.web.ApiResponse;

@Tag(name = "Auth")
@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthResource(RegisterUserUseCase registerUserUseCase, LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @POST
    @Path("/register")
    public Response register(@Valid RegisterUserCommand command) {
        AuthUserResponse response = registerUserUseCase.execute(command);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(Response.Status.CREATED.getStatusCode(), "User registered", response))
                .build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginCommand command) {
        AuthUserResponse response = loginUserUseCase.execute(command);
        return Response.ok(ApiResponse.success(Response.Status.OK.getStatusCode(), "Login successful", response))
                .build();
    }
}
