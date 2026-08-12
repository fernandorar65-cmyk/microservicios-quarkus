package kahoot.clabs.application.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.domain.aggregate.Organization;
import kahoot.clabs.domain.entity.OrganizationMember;

public record OrganizationProjectionSnapshot(
        UUID organizationId,
        String name,
        String slug,
        String logoUrl,
        String description,
        String timezone,
        String language,
        String status,
        List<OrganizationMemberPayload> members,
        int memberCount,
        Instant createdAt,
        Instant updatedAt
) {

    public static OrganizationProjectionSnapshot from(Organization organization) {
        List<OrganizationMemberPayload> members = organization.getMembers().stream()
                .map(OrganizationProjectionSnapshot::toMemberPayload)
                .toList();
        return new OrganizationProjectionSnapshot(
                organization.getId(),
                organization.getName().value(),
                organization.getSlug().value(),
                organization.getLogo(),
                organization.getDescription(),
                organization.getTimezone(),
                organization.getLanguage(),
                organization.getStatus().name(),
                members,
                members.size(),
                toInstant(organization.getCreatedAt()),
                toInstant(organization.getUpdatedAt()));
    }

    private static OrganizationMemberPayload toMemberPayload(OrganizationMember member) {
        return new OrganizationMemberPayload(
                member.getId(),
                member.getUserId(),
                member.getRoleId(),
                member.getStatus().name(),
                toInstant(member.getJoinedAt()));
    }

    private static Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
