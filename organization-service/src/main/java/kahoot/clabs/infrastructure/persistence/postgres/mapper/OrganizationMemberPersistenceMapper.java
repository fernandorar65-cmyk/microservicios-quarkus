package kahoot.clabs.infrastructure.persistence.postgres.mapper;

import java.time.LocalDateTime;

import kahoot.clabs.domain.entity.OrganizationMember;
import kahoot.clabs.domain.valueobject.MemberStatus;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationMemberJpaEntity;

public final class OrganizationMemberPersistenceMapper {

    private OrganizationMemberPersistenceMapper() {
    }

    public static OrganizationMemberJpaEntity toEntity(OrganizationMember member) {
        OrganizationMemberJpaEntity entity = OrganizationMemberJpaEntity.newInstance();
        LocalDateTime joinedAt = member.getJoinedAt() != null ? member.getJoinedAt() : LocalDateTime.now();
        entity.setId(member.getId());
        entity.setUserId(member.getUserId());
        entity.setRoleId(member.getRoleId());
        entity.setStatus(member.getStatus().name());
        entity.setJoinedAt(joinedAt);
        entity.setCreatedAt(member.getCreatedAt());
        entity.setUpdatedAt(member.getUpdatedAt());
        return entity;
    }

    public static OrganizationMember toDomain(OrganizationMemberJpaEntity entity) {
        return OrganizationMember.rehydrate(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getUserId(),
                entity.getRoleId(),
                MemberStatus.valueOf(entity.getStatus()),
                entity.getJoinedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
