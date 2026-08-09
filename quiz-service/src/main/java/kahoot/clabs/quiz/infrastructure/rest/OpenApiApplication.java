package kahoot.clabs.quiz.infrastructure.rest;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@OpenAPIDefinition(
        info = @Info(
                title = "Quiz Service API",
                version = "1.0.0",
                description = "Quiz authorship: quizzes, categories, questions, options and assets.",
                contact = @Contact(name = "Kahoot CLABS")),
        tags = {
                @Tag(name = "Quizzes", description = "Quiz lifecycle and content"),
                @Tag(name = "Categories", description = "Quiz categories"),
                @Tag(name = "Questions", description = "Questions, options and assets")
        })
public class OpenApiApplication {
}
