package kahoot.clabs.infrastructure.seed;

import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.event.GameSessionIntegrationEvent;
import kahoot.clabs.application.event.GameSessionProjectionSnapshot;
import kahoot.clabs.application.port.integration.GameSessionEventPublisher;
import kahoot.clabs.application.port.integration.QuizSnapshotPort;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.application.usecase.GameSessionSupport;
import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.repository.GameSessionRepository;
import kahoot.clabs.domain.valueobject.SessionStatus;
import kahoot.clabs.infrastructure.persistence.postgres.repository.GameSessionPanacheRepository;

/**
 * Dev seeder: Postgres inside JTA, then Kafka. Mongo only via session-events consumer.
 * Requires identity + organization + quiz seed and a PlayableQuizSnapshot in Mongo (quiz.events).
 */
@ApplicationScoped
public class GameplayDevDataSeeder {

    private static final Logger LOG = Logger.getLogger(GameplayDevDataSeeder.class);

    private static final String ORG_SLUG = "clabs";
    private static final String OWNER_EMAIL = "owner@kahoot-clabs.local";
    private static final String QUIZ_TITLE = "Java Basics";

    @Inject
    SharedDbSeedLookup sharedDbSeedLookup;

    @Inject
    QuizSnapshotPort quizSnapshotPort;

    @Inject
    GameSessionRepository gameSessionRepository;

    @Inject
    GameSessionPanacheRepository gameSessionPanacheRepository;

    @Inject
    GameSessionEventPublisher gameSessionEventPublisher;

    @ConfigProperty(name = "app.seed.enabled", defaultValue = "false")
    boolean seedEnabled;

    void onStart(@Observes StartupEvent event) {
        if (!seedEnabled) {
            return;
        }
        GameSession seeded = seedPostgres();
        if (seeded != null) {
            gameSessionEventPublisher.publish(
                    GameSessionIntegrationEvent.sessionCreated(
                            GameSessionProjectionSnapshot.from(seeded, QUIZ_TITLE, "Owner Demo")));
            LOG.infof("Gameplay seed session published id=%s", seeded.getId());
        }
        LOG.info("Gameplay seed completed (Postgres committed; Mongo via Kafka)");
    }

    @Transactional
    GameSession seedPostgres() {
        LOG.info("Seeding gameplay write model (Postgres only)");
        UUID organizationId = sharedDbSeedLookup.findOrganizationIdBySlug(ORG_SLUG).orElse(null);
        UUID hostUserId = sharedDbSeedLookup.findUserIdByEmail(OWNER_EMAIL).orElse(null);
        UUID quizId = organizationId == null
                ? null
                : sharedDbSeedLookup.findPublishedQuizId(organizationId, QUIZ_TITLE).orElse(null);

        if (organizationId == null || hostUserId == null || quizId == null) {
            LOG.warn("Skipping gameplay seed: org/host/quiz not found. "
                    + "Seed identity + organization + quiz first.");
            return null;
        }

        if (gameSessionPanacheRepository.countByOrganizationIdAndQuizIdAndStatus(
                organizationId, quizId, SessionStatus.LOBBY.name()) > 0) {
            LOG.infof("Lobby session already exists for quiz=%s — skipping", QUIZ_TITLE);
            return null;
        }

        PublishedQuizSnapshot snapshot = quizSnapshotPort
                .findPublishedByOrganizationAndId(organizationId, quizId)
                .orElse(null);
        if (snapshot == null) {
            LOG.warn("Skipping gameplay seed: PlayableQuizSnapshot not in Mongo yet. "
                    + "Wait for quiz.events consumption (restart gameplay after quiz seed).");
            return null;
        }

        GameSession session = GameSession.create(organizationId, quizId, hostUserId);
        GameSessionSupport.freezeFromSnapshot(session, snapshot);
        return gameSessionRepository.save(session);
    }
}
