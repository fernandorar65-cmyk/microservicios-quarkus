package kahoot.clabs.quiz.infrastructure.persistence.postgres.enums;

/**
 * Persistence enum for quiz lifecycle. Domain enum will be introduced later.
 */
public enum QuizStatusJpa {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}
