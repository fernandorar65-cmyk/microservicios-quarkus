package kahoot.clabs.infrastructure.persistence.postgres.mapper;

import java.util.List;

import kahoot.clabs.domain.aggregate.Organization;
import kahoot.clabs.domain.entity.OrganizationMember;
import kahoot.clabs.domain.valueobject.OrganizationStatus;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationJpaEntity;

public final class OrganizationPersistenceMapper {

    private OrganizationPersistenceMapper() {
    }

    public static OrganizationJpaEntity toEntity(Organization organization) {
        OrganizationJpaEntity entity = OrganizationJpaEntity.newInstance();
        entity.setId(organization.getId());
        entity.setName(organization.getName().value());
        entity.setSlug(organization.getSlug().value());
        entity.setLogoUrl(organization.getLogo());
        entity.setDescription(organization.getDescription());
        entity.setTimezone(organization.getTimezone());
        entity.setLanguage(organization.getLanguage());
        entity.setStatus(organization.getStatus().name());
        entity.setCreatedAt(organization.getCreatedAt());
        entity.setUpdatedAt(organization.getUpdatedAt());
        return entity;
    }

    public static Organization toDomain(OrganizationJpaEntity entity, List<OrganizationMember> members) {
        return Organization.rehydrate(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getLogoUrl(),
                entity.getDescription(),
                entity.getTimezone(),
                entity.getLanguage(),
                OrganizationStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                members);
    }
}
