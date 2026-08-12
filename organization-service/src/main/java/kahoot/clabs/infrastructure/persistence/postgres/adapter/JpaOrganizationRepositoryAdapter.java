package kahoot.clabs.infrastructure.persistence.postgres.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.domain.aggregate.Organization;
import kahoot.clabs.domain.entity.OrganizationMember;
import kahoot.clabs.domain.repository.OrganizationRepository;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationMemberJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.mapper.OrganizationMemberPersistenceMapper;
import kahoot.clabs.infrastructure.persistence.postgres.mapper.OrganizationPersistenceMapper;
import kahoot.clabs.infrastructure.persistence.postgres.repository.OrganizationMemberPanacheRepository;
import kahoot.clabs.infrastructure.persistence.postgres.repository.OrganizationPanacheRepository;

@ApplicationScoped
public class JpaOrganizationRepositoryAdapter implements OrganizationRepository {

    @Inject
    OrganizationPanacheRepository organizationRepository;

    @Inject
    OrganizationMemberPanacheRepository memberRepository;

    @Override
    @Transactional
    public Organization save(Organization organization) {
        OrganizationJpaEntity entity = OrganizationPersistenceMapper.toEntity(organization);
        OrganizationJpaEntity saved = organizationRepository.getEntityManager().merge(entity);
        List<OrganizationMember> members = organization.getMembers();
        syncMembers(saved, members);
        return OrganizationPersistenceMapper.toDomain(saved, members);
    }

    @Override
    public Optional<Organization> findById(UUID id) {
        return organizationRepository.findByIdOptional(id).map(this::toAggregate);
    }

    @Override
    public Optional<Organization> findBySlug(String slug) {
        return organizationRepository.findBySlug(slug).map(this::toAggregate);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return organizationRepository.existsBySlug(slug);
    }

    @Override
    @Transactional
    public void delete(Organization organization) {
        memberRepository.deleteByOrganizationId(organization.getId());
        organizationRepository.deleteById(organization.getId());
    }

    private Organization toAggregate(OrganizationJpaEntity entity) {
        List<OrganizationMember> members = memberRepository.findByOrganizationId(entity.getId()).stream()
                .map(OrganizationMemberPersistenceMapper::toDomain)
                .toList();
        return OrganizationPersistenceMapper.toDomain(entity, members);
    }

    private void syncMembers(OrganizationJpaEntity organizationEntity, List<OrganizationMember> members) {
        UUID organizationId = organizationEntity.getId();
        List<UUID> currentIds = members.stream().map(OrganizationMember::getId).toList();
        if (currentIds.isEmpty()) {
            memberRepository.deleteByOrganizationId(organizationId);
            return;
        }
        memberRepository.deleteByOrganizationIdAndIdNotIn(organizationId, currentIds);
        for (OrganizationMember member : members) {
            OrganizationMemberJpaEntity memberEntity = OrganizationMemberPersistenceMapper.toEntity(member);
            memberEntity.setOrganization(organizationEntity);
            memberRepository.getEntityManager().merge(memberEntity);
        }
    }
}
