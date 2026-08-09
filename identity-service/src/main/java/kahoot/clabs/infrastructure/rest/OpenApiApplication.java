package kahoot.clabs.infrastructure.rest;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@OpenAPIDefinition(
        info = @Info(
                title = "Identity Service API",
                version = "1.0.0",
                description = "Auth, users, roles and permissions (Kahoot CLABS).",
                contact = @Contact(name = "Kahoot CLABS")),
        tags = {
                @Tag(name = "Auth", description = "Login and registration"),
                @Tag(name = "Users", description = "User profile and administration"),
                @Tag(name = "Roles", description = "Roles and permissions")
        })
public class OpenApiApplication {
}
