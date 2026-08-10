package kahoot.clabs.application.usecase;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import kahoot.clabs.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot.AnswerOptionSnapshot;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot.QuestionSnapshot;
import kahoot.clabs.domain.aggregate.GameSession;
import kahoot.clabs.domain.entity.SessionAnswerOption;
import kahoot.clabs.domain.entity.SessionQuestion;
import kahoot.clabs.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.domain.repository.GameSessionRepository;
import kahoot.clabs.domain.shared.DomainException;

final class GameSessionSupport {

    private GameSessionSupport() {
    }

    static void requireOrganization(OrganizationMembershipPort membershipPort, UUID organizationId) {
        if (!membershipPort.organizationExists(organizationId)) {
            throw new DomainException("Organization not found: " + organizationId);
        }
    }

    static void requireMember(OrganizationMembershipPort membershipPort, UUID organizationId, UUID userId) {
        if (!membershipPort.isActiveMember(organizationId, userId)) {
            throw new DomainException("User is not a member of this organization: " + userId);
        }
    }

    static GameSession requireSession(
            GameSessionRepository sessionRepository,
            UUID organizationId,
            UUID sessionId) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
        session.ensureBelongsTo(organizationId);
        return session;
    }

    static void freezeFromSnapshot(GameSession session, PublishedQuizSnapshot snapshot) {
        if (!session.getQuestions().isEmpty()) {
            return;
        }
        List<QuestionSnapshot> sourceQuestions = snapshot.questions();
        if (sourceQuestions == null || sourceQuestions.isEmpty()) {
            throw new DomainException("Quiz has no questions to freeze");
        }
        List<QuestionSnapshot> ordered = sourceQuestions.stream()
                .sorted(Comparator.comparingInt(QuestionSnapshot::orderIndex))
                .toList();
        AtomicInteger index = new AtomicInteger(0);
        List<SessionQuestion> frozen = ordered.stream()
                .map(question -> toFrozenQuestion(session.getId(), question, index.getAndIncrement()))
                .toList();
        session.freezeQuestions(frozen);
    }

    private static SessionQuestion toFrozenQuestion(UUID sessionId, QuestionSnapshot question, int zeroBasedIndex) {
        List<AnswerOptionSnapshot> sortedOptions = question.options().stream()
                .sorted(Comparator.comparingInt(AnswerOptionSnapshot::orderIndex))
                .toList();
        AtomicInteger optionIndex = new AtomicInteger(0);
        List<SessionAnswerOption> frozenOptions = sortedOptions.stream()
                .map(option -> SessionAnswerOption.freeze(
                        null,
                        option.id(),
                        option.text(),
                        option.correct(),
                        optionIndex.getAndIncrement()))
                .toList();
        return SessionQuestion.freeze(
                sessionId,
                question.id(),
                zeroBasedIndex,
                question.points(),
                question.timeLimitSeconds(),
                question.title(),
                question.description(),
                question.type(),
                frozenOptions);
    }
}
