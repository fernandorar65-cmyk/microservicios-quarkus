package kahoot.clabs.infrastructure.seed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot.AnswerOptionSnapshot;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot.QuestionSnapshot;
import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.entity.SessionAnswerOption;
import kahoot.clabs.domain.entity.SessionQuestion;
import kahoot.clabs.domain.repository.GameSessionRepository;
import kahoot.clabs.domain.shared.DomainException;
import kahoot.clabs.domain.valueobject.SessionStatus;

/**
 * Demo sessions for Clabs. Builds local published-quiz snapshots (no quiz-service DB / stub).
 */
@ApplicationScoped
public class GameplayClabsDemoSeeder implements DataSeeder {

    private static final Logger LOG = Logger.getLogger(GameplayClabsDemoSeeder.class);

    private final GameSessionRepository gameSessionRepository;

    @Inject
    public GameplayClabsDemoSeeder(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Override
    public int order() {
        return 50;
    }

    @Override
    public String name() {
        return "gameplay-clabs-demo";
    }

    @Override
    public void seed() {
        if (gameSessionRepository.findById(SeedIds.SESSION_LOBBY).isPresent()) {
            LOG.info("Gameplay seed skipped (SESSION_LOBBY already exists)");
            return;
        }

        UUID orgId = SeedIds.ORG_CLABS;
        UUID hostId = SeedIds.USER_OWNER;

        List<Joiner> lobbyJoiners = List.of(
                new Joiner(SeedIds.USER_MEMBER, "Member"),
                new Joiner(SeedIds.demoUser("valentina.rios@clabs.local"), "Vale"),
                new Joiner(SeedIds.demoUser("andres.salazar@clabs.local"), "Andres"),
                new Joiner(SeedIds.demoUser("camila.vargas@clabs.local"), "Cami"));

        seedLobbySession(orgId, SeedIds.QUIZ_JAVA, hostId, lobbyJoiners);
        seedFinishedSession(
                orgId,
                SeedIds.QUIZ_CULTURE,
                hostId,
                List.of(
                        new Joiner(SeedIds.USER_MEMBER, "Member"),
                        new Joiner(SeedIds.demoUser("valentina.rios@clabs.local"), "Vale"),
                        new Joiner(SeedIds.demoUser("andres.salazar@clabs.local"), "Andres")));
        seedCancelledSession(orgId, SeedIds.QUIZ_DEVOPS, hostId);
        LOG.info("Gameplay Clabs demo sessions seeded");
    }

    private void seedLobbySession(UUID orgId, UUID quizId, UUID hostId, List<Joiner> joiners) {
        GameSession session = createFrozenSession(SeedIds.SESSION_LOBBY, orgId, quizId, hostId);
        session = gameSessionRepository.save(session);
        for (Joiner joiner : joiners) {
            session.join(joiner.userId(), joiner.nickname());
        }
        gameSessionRepository.save(session);
    }

    private void seedFinishedSession(UUID orgId, UUID quizId, UUID hostId, List<Joiner> joiners) {
        GameSession session = createFrozenSession(SeedIds.SESSION_FINISHED, orgId, quizId, hostId);
        session = gameSessionRepository.save(session);
        for (Joiner joiner : joiners) {
            session.join(joiner.userId(), joiner.nickname());
        }

        session.start();
        while (true) {
            SessionQuestion current = session.findCurrentQuestion()
                    .orElseThrow(() -> new DomainException("Missing current question in seed"));
            UUID correctOptionId = current.getOptions().stream()
                    .filter(SessionAnswerOption::isCorrect)
                    .map(SessionAnswerOption::getId)
                    .findFirst()
                    .orElse(null);

            for (Joiner joiner : joiners) {
                session.submitAnswer(joiner.userId(), correctOptionId);
            }

            session.closeQuestion();
            int before = session.getCurrentQuestionIndex();
            session.nextQuestion();
            if (session.getStatus().isTerminal() || session.getCurrentQuestionIndex() == before) {
                break;
            }
        }

        if (!session.getStatus().isTerminal()) {
            session.finish();
        }
        gameSessionRepository.save(session);
    }

    private void seedCancelledSession(UUID orgId, UUID quizId, UUID hostId) {
        GameSession session = createFrozenSession(SeedIds.SESSION_CANCELLED, orgId, quizId, hostId);
        session = gameSessionRepository.save(session);
        session.cancel();
        gameSessionRepository.save(session);
    }

    private GameSession createFrozenSession(UUID sessionId, UUID orgId, UUID quizId, UUID hostUserId) {
        PublishedQuizSnapshot snapshot = demoSnapshot(orgId, quizId);
        LocalDateTime now = LocalDateTime.now();
        GameSession session = GameSession.rehydrate(
                sessionId,
                orgId,
                quizId,
                hostUserId,
                SessionStatus.LOBBY,
                0,
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                now,
                now);
        // freeze via same logic as use cases — package-private support is in same module
        freeze(session, snapshot);
        return session;
    }

    private static void freeze(GameSession session, PublishedQuizSnapshot snapshot) {
        // Inline freeze to avoid depending on package-private GameSessionSupport from another package
        var ordered = snapshot.questions().stream()
                .sorted(java.util.Comparator.comparingInt(QuestionSnapshot::orderIndex))
                .toList();
        java.util.concurrent.atomic.AtomicInteger index = new java.util.concurrent.atomic.AtomicInteger(0);
        List<SessionQuestion> frozen = ordered.stream()
                .map(question -> {
                    var sortedOptions = question.options().stream()
                            .sorted(java.util.Comparator.comparingInt(AnswerOptionSnapshot::orderIndex))
                            .toList();
                    java.util.concurrent.atomic.AtomicInteger optionIndex =
                            new java.util.concurrent.atomic.AtomicInteger(0);
                    List<SessionAnswerOption> options = sortedOptions.stream()
                            .map(option -> SessionAnswerOption.freeze(
                                    null,
                                    option.id(),
                                    option.text(),
                                    option.correct(),
                                    optionIndex.getAndIncrement()))
                            .toList();
                    return SessionQuestion.freeze(
                            session.getId(),
                            question.id(),
                            index.getAndIncrement(),
                            question.points(),
                            question.timeLimitSeconds(),
                            question.title(),
                            question.description(),
                            question.type(),
                            options);
                })
                .toList();
        session.freezeQuestions(frozen);
    }

    /**
     * Minimal published snapshot mirroring seed quiz content enough to run sessions.
     * Not loaded from quiz-service (bounded context isolation).
     */
    private static PublishedQuizSnapshot demoSnapshot(UUID organizationId, UUID quizId) {
        if (SeedIds.QUIZ_JAVA.equals(quizId)) {
            return new PublishedQuizSnapshot(quizId, organizationId, List.of(
                    mc("¿Qué palabra clave declara una constante en Java?", 1,
                            opt("final", true), opt("const", false), opt("static", false)),
                    tf("En Java, String es un tipo primitivo.", 2, false),
                    mc("¿Cuál es el modificador de acceso más restrictivo?", 3,
                            opt("private", true), opt("protected", false), opt("public", false))));
        }
        if (SeedIds.QUIZ_CULTURE.equals(quizId)) {
            return new PublishedQuizSnapshot(quizId, organizationId, List.of(
                    mc("¿Qué práctica ayuda más a un buen code review?", 1,
                            opt("Dar feedback concreto y respetuoso", true),
                            opt("Aprobar sin leer", false)),
                    tf("Documentar decisiones técnicas facilita el onboarding.", 2, true),
                    mc("En una retrospección, ¿qué conviene priorizar?", 3,
                            opt("Acciones concretas de mejora", true),
                            opt("Buscar culpables", false))));
        }
        if (SeedIds.QUIZ_DEVOPS.equals(quizId)) {
            return new PublishedQuizSnapshot(quizId, organizationId, List.of(
                    mc("¿Qué significa CI en CI/CD?", 1,
                            opt("Continuous Integration", true),
                            opt("Cloud Infrastructure", false)),
                    tf("Un rollback rápido es útil ante un incidente de deploy.", 2, true)));
        }
        throw new IllegalStateException("No demo snapshot for quiz " + quizId);
    }

    private static QuestionSnapshot mc(String title, int order, Option... options) {
        List<AnswerOptionSnapshot> opts = new java.util.ArrayList<>();
        for (int i = 0; i < options.length; i++) {
            Option o = options[i];
            opts.add(new AnswerOptionSnapshot(UUID.randomUUID(), o.text(), o.correct(), i));
        }
        return new QuestionSnapshot(
                UUID.randomUUID(), order, 1000, 20, title, null, "MULTIPLE_CHOICE", opts);
    }

    private static QuestionSnapshot tf(String title, int order, boolean correctIsTrue) {
        return new QuestionSnapshot(
                UUID.randomUUID(),
                order,
                800,
                15,
                title,
                null,
                "TRUE_FALSE",
                List.of(
                        new AnswerOptionSnapshot(UUID.randomUUID(), "Verdadero", correctIsTrue, 0),
                        new AnswerOptionSnapshot(UUID.randomUUID(), "Falso", !correctIsTrue, 1)));
    }

    private static Option opt(String text, boolean correct) {
        return new Option(text, correct);
    }

    private record Option(String text, boolean correct) {
    }

    private record Joiner(UUID userId, String nickname) {
    }
}
