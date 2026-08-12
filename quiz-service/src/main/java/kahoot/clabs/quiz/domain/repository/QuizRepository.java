package kahoot.clabs.quiz.domain.repository;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.quiz.domain.aggregate.Quiz;

public interface QuizRepository {

    Quiz save(Quiz quiz);

    Optional<Quiz> findById(UUID id);

    boolean existsById(UUID id);

    boolean existsByOrganizationIdAndTitleIgnoreCase(UUID organizationId, String title);

    void delete(Quiz quiz);

    void deleteById(UUID id);
}
