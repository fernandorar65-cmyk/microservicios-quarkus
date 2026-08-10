package kahoot.clabs.application.dto;

import java.util.List;
import java.util.UUID;

import kahoot.clabs.application.readmodel.OrganizationReadModel;
import kahoot.clabs.domain.aggregate.Organization;

public record OrganizationResponse(
        UUID id,
        String name,
        String slug,
        String description,
        String logo,
        String timezone,
        String language,
        String status,
        List<OrganizationMemberResponse> members) {

    public static OrganizationResponse from(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName().value(),
                organization.getSlug().value(),
                organization.getDescription(),
                organization.getLogo(),
                organization.getTimezone(),
                organization.getLanguage(),
                organization.getStatus().name(),
                organization.getMembers().stream()
                        .map(OrganizationMemberResponse::from)
                        .toList());
    }

    public static OrganizationResponse from(OrganizationReadModel readModel) {
        return new OrganizationResponse(
                readModel.getId(),
                readModel.getName(),
                readModel.getSlug(),
                readModel.getDescription(),
                readModel.getLogoUrl(),
                readModel.getTimezone(),
                readModel.getLanguage(),
                readModel.getStatus(),
                readModel.getMembers() == null
                        ? List.of()
                        : readModel.getMembers().stream()
                                .map(OrganizationMemberResponse::from)
                                .toList());
    }
}
