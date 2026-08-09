package kahoot.clabs.infrastructure.rest;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@OpenAPIDefinition(
        info = @Info(
                title = "Gameplay Service API",
                version = "1.0.0",
                description = "Live game sessions, answers and leaderboard.",
                contact = @Contact(name = "Kahoot CLABS")),
        tags = {
                @Tag(name = "Sessions", description = "Session lifecycle and lobby"),
                @Tag(name = "Play", description = "Questions, answers and results"),
                @Tag(name = "Leaderboard", description = "Session rankings")
        })
public class OpenApiApplication {
}
