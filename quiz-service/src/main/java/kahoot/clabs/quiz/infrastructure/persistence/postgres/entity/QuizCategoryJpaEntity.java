package kahoot.clabs.quiz.infrastructure.persistence.postgres.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA persistence model for Quiz ↔ Category many-to-many association.
 * Category retains its own lifecycle; only the association row is owned here.
 */
@Entity
@Table(name = "quiz_categories")
@IdClass(QuizCategoryId.class)
public class QuizCategoryJpaEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizJpaEntity quiz;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryJpaEntity category;

    protected QuizCategoryJpaEntity() {
    }

    public QuizCategoryJpaEntity(QuizJpaEntity quiz, CategoryJpaEntity category) {
        this.quiz = quiz;
        this.category = category;
    }

    public QuizJpaEntity getQuiz() {
        return quiz;
    }

    public void setQuiz(QuizJpaEntity quiz) {
        this.quiz = quiz;
    }

    public CategoryJpaEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryJpaEntity category) {
        this.category = category;
    }
}
