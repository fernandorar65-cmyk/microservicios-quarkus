package kahoot.clabs.quiz.infrastructure.persistence.postgres.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quiz_categories")
@IdClass(QuizCategoryId.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizCategoryJpaEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizJpaEntity quiz;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryJpaEntity category;

    public QuizCategoryJpaEntity(QuizJpaEntity quiz, CategoryJpaEntity category) {
        this.quiz = quiz;
        this.category = category;
    }

    public static QuizCategoryJpaEntity link(QuizJpaEntity quiz, UUID categoryId) {
        return new QuizCategoryJpaEntity(quiz, CategoryJpaEntity.reference(categoryId));
    }
}
