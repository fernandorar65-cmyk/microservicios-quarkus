package kahoot.clabs.application.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import kahoot.clabs.application.readmodel.OrganizationMemberReadModel;
import kahoot.clabs.domain.entity.OrganizationMember;

public record OrganizationMemberResponse(
        UUID id,
        UUID userId,
        UUID roleId,
        String status,
        Instant joinedAt) {

    public static OrganizationMemberResponse from(OrganizationMember member) {
        return new OrganizationMemberResponse(
                member.getId(),
                member.getUserId(),
                member.getRoleId(),
                member.getStatus().name(),
                toInstant(member.getJoinedAt()));
    }

    public static OrganizationMemberResponse from(OrganizationMemberReadModel member) {
        return new OrganizationMemberResponse(
                member.getId(),
                member.getUserId(),
                member.getRoleId(),
                member.getStatus(),
                member.getJoinedAt());
    }

    private static Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
