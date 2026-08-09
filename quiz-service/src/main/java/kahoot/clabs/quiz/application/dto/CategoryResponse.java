package kahoot.clabs.quiz.application.dto;

import java.util.UUID;

import kahoot.clabs.quiz.application.readmodel.CategoryReadModel;
import kahoot.clabs.quiz.domain.entity.Category;

public record CategoryResponse(
        UUID id,
        UUID organizationId,
        String name,
        String description,
        String color,
        String icon) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getOrganizationId(),
                category.getName(),
                category.getDescription(),
                category.getColor(),
                category.getIcon());
    }

    public static CategoryResponse from(CategoryReadModel readModel) {
        return new CategoryResponse(
                readModel.getId(),
                readModel.getOrganizationId(),
                readModel.getName(),
                readModel.getDescription(),
                readModel.getColor(),
                readModel.getIcon());
    }
}
