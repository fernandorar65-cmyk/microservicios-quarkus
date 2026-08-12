package kahoot.clabs.quiz.infrastructure.seed;

import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.entity.Category;
import kahoot.clabs.quiz.domain.entity.Question;
import kahoot.clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.quiz.domain.valueobject.QuestionType;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.mapper.CategoryMapper;
import kahoot.clabs.quiz.infrastructure.persistence.postgres.repository.CategoryPanacheRepository;

/**
 * Dev seeder: Postgres inside JTA. Mongo + gameplay snapshot via Kafka after commit
 * ({@code quiz.read.events} and {@code quiz.events}).
 */
@ApplicationScoped
public class QuizDevDataSeeder {

    private static final Logger LOG = Logger.getLogger(QuizDevDataSeeder.class);

    private static final String ORG_SLUG = "clabs";
    private static final String OWNER_EMAIL = "owner@kahoot-clabs.local";
    private static final String CATEGORY_NAME = "Java";
    private static final String QUIZ_TITLE = "Java Basics";

    @Inject
    SharedDbSeedLookup sharedDbSeedLookup;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CategoryPanacheRepository categoryPanacheRepository;

    @Inject
    QuizRepository quizRepository;

    @ConfigProperty(name = "app.seed.enabled", defaultValue = "false")
    boolean seedEnabled;

    void onStart(@Observes StartupEvent event) {
        if (!seedEnabled) {
            return;
        }
        Quiz seeded = seedPostgres();
        if (seeded != null) {
            LOG.infof(
                    "Quiz seed write committed title=%s id=%s — Mongo/Kafka projection async",
                    QUIZ_TITLE,
                    seeded.getId());
        }
        LOG.info("Quiz seed completed (Postgres committed; Mongo via Kafka consumers)");
    }

    @Transactional
    Quiz seedPostgres() {
        LOG.info("Seeding quiz write model (Postgres only)");
        UUID organizationId = sharedDbSeedLookup.findOrganizationIdBySlug(ORG_SLUG).orElse(null);
        UUID createdById = sharedDbSeedLookup.findUserIdByEmail(OWNER_EMAIL).orElse(null);
        if (organizationId == null || createdById == null) {
            LOG.warn("Skipping quiz seed: organization/user not found. "
                    + "Seed identity + organization first.");
            return null;
        }

        Category category = ensureCategory(organizationId);
        if (quizRepository.existsByOrganizationIdAndTitleIgnoreCase(organizationId, QUIZ_TITLE)) {
            LOG.infof("Quiz already exists title=%s — skipping create", QUIZ_TITLE);
            return null;
        }

        Quiz quiz = Quiz.create(organizationId, QUIZ_TITLE, createdById);
        quiz.changeDescription("Quiz demo mínimo de Java");
        quiz.assignCategory(category.getId());
        Question question = quiz.addQuestion(
                "¿Qué palabra clave declara una constante en Java?",
                QuestionType.MULTIPLE_CHOICE);
        quiz.addAnswerOption(question.getId(), "final", true);
        quiz.addAnswerOption(question.getId(), "const", false);
        quiz.publish();
        return quizRepository.save(quiz);
    }

    private Category ensureCategory(UUID organizationId) {
        return categoryPanacheRepository
                .findByOrganizationIdAndNameIgnoreCase(organizationId, CATEGORY_NAME)
                .map(CategoryMapper::toDomain)
                .orElseGet(() -> {
                    Category created = Category.create(organizationId, CATEGORY_NAME);
                    created.changeDescription("Preguntas de Java");
                    created.changeColor("#F89820");
                    created.changeIcon("coffee");
                    Category saved = categoryRepository.save(created);
                    LOG.infof("Seeded category name=%s id=%s", CATEGORY_NAME, saved.getId());
                    return saved;
                });
    }
}
