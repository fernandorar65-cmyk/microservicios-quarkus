package kahoot.clabs.quiz.application.readmodel;

import java.time.Instant;
import java.time.ZoneOffset;

import kahoot.clabs.quiz.domain.entity.Category;

public final class CategoryReadModels {

    private CategoryReadModels() {
    }

    public static CategoryReadModel from(Category category) {
        CategoryReadModel readModel = new CategoryReadModel();
        readModel.setId(category.getId());
        readModel.setOrganizationId(category.getOrganizationId());
        readModel.setName(category.getName());
        readModel.setDescription(category.getDescription());
        readModel.setColor(category.getColor());
        readModel.setIcon(category.getIcon());
        readModel.setQuizCount(0);
        readModel.setCreatedAt(Instant.now());
        readModel.setUpdatedAt(Instant.now());
        return readModel;
    }

    public static CategoryReadModel from(Category category, Instant createdAt, Instant updatedAt, int quizCount) {
        CategoryReadModel readModel = from(category);
        readModel.setCreatedAt(createdAt);
        readModel.setUpdatedAt(updatedAt);
        readModel.setQuizCount(quizCount);
        return readModel;
    }

    public static Instant toInstant(java.time.LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
