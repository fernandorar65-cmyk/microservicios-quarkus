package kahoot.clabs.infrastructure.seed;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import kahoot.clabs.domain.valueobject.MemberStatus;
import kahoot.clabs.domain.valueobject.OrganizationStatus;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationMemberStatusJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationStatusJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.repository.OrganizationMemberStatusCatalogPanacheRepository;
import kahoot.clabs.infrastructure.persistence.postgres.repository.OrganizationStatusCatalogPanacheRepository;

@ApplicationScoped
public class OrganizationReferenceDataSeeder implements DataSeeder {

    private static final Logger LOG = Logger.getLogger(OrganizationReferenceDataSeeder.class);

    private final OrganizationStatusCatalogPanacheRepository organizationStatusRepository;
    private final OrganizationMemberStatusCatalogPanacheRepository memberStatusRepository;

    @Inject
    public OrganizationReferenceDataSeeder(
            OrganizationStatusCatalogPanacheRepository organizationStatusRepository,
            OrganizationMemberStatusCatalogPanacheRepository memberStatusRepository) {
        this.organizationStatusRepository = organizationStatusRepository;
        this.memberStatusRepository = memberStatusRepository;
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public String name() {
        return "organization-reference-data";
    }

    @Override
    public void seed() {
        seedOrganizationStatuses();
        seedMemberStatuses();
    }

    private void seedOrganizationStatuses() {
        for (OrganizationStatus status : OrganizationStatus.values()) {
            if (organizationStatusRepository.existsByName(status.name())) {
                continue;
            }
            OrganizationStatusJpaEntity entity = OrganizationStatusJpaEntity.newInstance();
            entity.setId(UUID.randomUUID());
            entity.setName(status.name());
            entity.setDescription(truncate(status.getDescription(), 100));
            organizationStatusRepository.persist(entity);
        }
        LOG.debug("Organization status catalog seeded");
    }

    private void seedMemberStatuses() {
        for (MemberStatus status : MemberStatus.values()) {
            if (memberStatusRepository.existsByName(status.name())) {
                continue;
            }
            OrganizationMemberStatusJpaEntity entity = OrganizationMemberStatusJpaEntity.newInstance();
            entity.setId(UUID.randomUUID());
            entity.setName(status.name());
            entity.setDescription(truncate(status.getDescription(), 100));
            memberStatusRepository.persist(entity);
        }
        LOG.debug("Member status catalog seeded");
    }

    private static String truncate(String value, int max) {
        if (value == null || value.isBlank()) {
            return "description base";
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
