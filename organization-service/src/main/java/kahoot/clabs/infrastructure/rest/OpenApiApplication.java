package kahoot.clabs.infrastructure.rest;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@OpenAPIDefinition(
        info = @Info(
                title = "Organization Service API",
                version = "1.0.0",
                description = "Organizations, members, departments, jobs and status catalogs.",
                contact = @Contact(name = "Kahoot CLABS")),
        tags = {
                @Tag(name = "Organizations", description = "Organization CRUD and localization"),
                @Tag(name = "Members", description = "Membership and invitations"),
                @Tag(name = "Catalogs", description = "Departments, jobs and statuses")
        })
public class OpenApiApplication {
}
