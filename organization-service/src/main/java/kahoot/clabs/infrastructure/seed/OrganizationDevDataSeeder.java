package kahoot.clabs.infrastructure.seed;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.event.CatalogItemProjectionSnapshot;
import kahoot.clabs.application.event.CatalogItemUpsertedEvent;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;
import kahoot.clabs.application.event.OrganizationProjectionSnapshot;
import kahoot.clabs.application.port.integration.OrganizationEventPublisher;
import kahoot.clabs.application.port.integration.UserDirectoryPort;
import kahoot.clabs.domain.aggregate.Organization;
import kahoot.clabs.domain.entity.OrganizationDepartment;
import kahoot.clabs.domain.entity.OrganizationJob;
import kahoot.clabs.domain.entity.OrganizationMemberStatusCatalog;
import kahoot.clabs.domain.entity.OrganizationStatusCatalog;
import kahoot.clabs.domain.repository.OrganizationRepository;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationDepartmentJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationJobJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationMemberStatusJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationStatusJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.repository.OrganizationDepartmentPanacheRepository;
import kahoot.clabs.infrastructure.persistence.postgres.repository.OrganizationJobPanacheRepository;
import kahoot.clabs.infrastructure.persistence.postgres.repository.OrganizationMemberStatusCatalogPanacheRepository;
import kahoot.clabs.infrastructure.persistence.postgres.repository.OrganizationStatusCatalogPanacheRepository;

@ApplicationScoped
public class OrganizationDevDataSeeder {

    private static final Logger LOG = Logger.getLogger(OrganizationDevDataSeeder.class);

    private static final String ORG_SLUG = "clabs";
    private static final String OWNER_EMAIL = "owner@kahoot-clabs.local";
    private static final String MEMBER_EMAIL = "member@kahoot-clabs.local";

    @Inject
    OrganizationRepository organizationRepository;

    @Inject
    OrganizationEventPublisher organizationEventPublisher;

    @Inject
    UserDirectoryPort userDirectoryPort;

    @Inject
    OrganizationStatusCatalogPanacheRepository statusCatalogRepository;

    @Inject
    OrganizationMemberStatusCatalogPanacheRepository memberStatusCatalogRepository;

    @Inject
    OrganizationDepartmentPanacheRepository departmentRepository;

    @Inject
    OrganizationJobPanacheRepository jobRepository;

    @ConfigProperty(name = "app.seed.enabled", defaultValue = "false")
    boolean seedEnabled;

    void onStart(@Observes StartupEvent event) {
        if (!seedEnabled) {
            return;
        }
        SeedBatch batch = seedPostgres();
        publishKafka(batch);
        LOG.info("Organization seed completed (Postgres committed; Mongo via Kafka)");
    }

    @Transactional
    SeedBatch seedPostgres() {
        LOG.info("Seeding organization write model (Postgres only)");
        List<CatalogItemProjectionSnapshot> catalogs = new ArrayList<>();
        catalogs.add(ensureStatus(OrganizationStatusCatalog.create("ACTIVE", "Activo")));
        catalogs.add(ensureStatus(OrganizationStatusCatalog.create("INACTIVE", "Inactivo")));
        catalogs.add(ensureStatus(OrganizationStatusCatalog.create("SUSPENDED", "Suspendido")));
        catalogs.add(ensureStatus(OrganizationStatusCatalog.create("PENDING", "Pendiente de activación")));

        catalogs.add(ensureMemberStatus(OrganizationMemberStatusCatalog.create("INVITED", "Invitado")));
        catalogs.add(ensureMemberStatus(OrganizationMemberStatusCatalog.create("ACTIVE", "Activo")));
        catalogs.add(ensureMemberStatus(OrganizationMemberStatusCatalog.create("SUSPENDED", "Suspendido")));

        catalogs.add(ensureDepartment(
                OrganizationDepartment.create("Ingeniería de Software", "Desarrollo y mantenimiento")));
        catalogs.add(ensureDepartment(OrganizationDepartment.create("DevOps", "CI/CD y releases")));

        catalogs.add(ensureJob(OrganizationJob.create("Software Engineer", "Backend/frontend features")));
        catalogs.add(ensureJob(OrganizationJob.create("DevOps Engineer", "Deploy y monitoreo")));

        return new SeedBatch(catalogs, seedOrganization());
    }

    private void publishKafka(SeedBatch batch) {
        for (CatalogItemProjectionSnapshot catalog : batch.catalogs()) {
            organizationEventPublisher.publish(CatalogItemUpsertedEvent.of(catalog));
        }
        if (batch.organization() != null) {
            organizationEventPublisher.publish(
                    OrganizationIntegrationEvent.organizationCreated(
                            OrganizationProjectionSnapshot.from(batch.organization())));
            LOG.infof(
                    "Published OrganizationCreated slug=%s id=%s",
                    ORG_SLUG,
                    batch.organization().getId());
        }
    }

    private CatalogItemProjectionSnapshot ensureStatus(OrganizationStatusCatalog catalog) {
        OrganizationStatusJpaEntity entity;
        if (statusCatalogRepository.count("name", catalog.getName()) > 0) {
            entity = statusCatalogRepository.find("name", catalog.getName()).firstResult();
        } else {
            entity = OrganizationStatusJpaEntity.newInstance();
            entity.setId(catalog.getId());
            entity.setName(catalog.getName());
            entity.setDescription(catalog.getDescription());
            statusCatalogRepository.persist(entity);
        }
        return new CatalogItemProjectionSnapshot(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                CatalogItemProjectionSnapshot.KIND_STATUS);
    }

    private CatalogItemProjectionSnapshot ensureMemberStatus(OrganizationMemberStatusCatalog catalog) {
        OrganizationMemberStatusJpaEntity entity;
        if (memberStatusCatalogRepository.count("name", catalog.getName()) > 0) {
            entity = memberStatusCatalogRepository.find("name", catalog.getName()).firstResult();
        } else {
            entity = OrganizationMemberStatusJpaEntity.newInstance();
            entity.setId(catalog.getId());
            entity.setName(catalog.getName());
            entity.setDescription(catalog.getDescription());
            memberStatusCatalogRepository.persist(entity);
        }
        return new CatalogItemProjectionSnapshot(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                CatalogItemProjectionSnapshot.KIND_MEMBER_STATUS);
    }

    private CatalogItemProjectionSnapshot ensureDepartment(OrganizationDepartment department) {
        OrganizationDepartmentJpaEntity entity;
        if (departmentRepository.existsByName(department.getName())) {
            entity = departmentRepository.find("name", department.getName()).firstResult();
        } else {
            entity = OrganizationDepartmentJpaEntity.newInstance();
            entity.setId(department.getId());
            entity.setName(department.getName());
            entity.setDescription(department.getDescription());
            departmentRepository.persist(entity);
        }
        return new CatalogItemProjectionSnapshot(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                CatalogItemProjectionSnapshot.KIND_DEPARTMENT);
    }

    private CatalogItemProjectionSnapshot ensureJob(OrganizationJob job) {
        OrganizationJobJpaEntity entity;
        if (jobRepository.count("name", job.getName()) > 0) {
            entity = jobRepository.find("name", job.getName()).firstResult();
        } else {
            entity = OrganizationJobJpaEntity.newInstance();
            entity.setId(job.getId());
            entity.setName(job.getName());
            entity.setDescription(job.getDescription());
            jobRepository.persist(entity);
        }
        return new CatalogItemProjectionSnapshot(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                CatalogItemProjectionSnapshot.KIND_JOB);
    }

    private Organization seedOrganization() {
        UUID ownerUserId = userDirectoryPort.findUserIdByEmail(OWNER_EMAIL).orElse(null);
        UUID ownerRoleId = userDirectoryPort.findRoleIdByType("OWNER_ORGANIZATION").orElse(null);
        UUID memberUserId = userDirectoryPort.findUserIdByEmail(MEMBER_EMAIL).orElse(null);
        UUID memberRoleId = userDirectoryPort.findRoleIdByType("COMMON_MEMBER").orElse(null);

        if (ownerUserId == null || ownerRoleId == null) {
            LOG.warn("Skipping organization aggregate seed: identity users/roles not found yet. "
                    + "Start identity-service with app.seed.enabled=true first.");
            return null;
        }

        Organization organization = organizationRepository.findBySlug(ORG_SLUG).orElseGet(() -> {
            Organization created = Organization.create("Clabs", ORG_SLUG);
            created.updateDetails("Clabs", "Organización demo Kahoot CLABS");
            created.addMember(ownerUserId, ownerRoleId);
            if (memberUserId != null && memberRoleId != null) {
                created.addMember(memberUserId, memberRoleId);
            }
            return organizationRepository.save(created);
        });

        boolean changed = false;
        if (!organization.hasMember(ownerUserId)) {
            organization.addMember(ownerUserId, ownerRoleId);
            changed = true;
        }
        if (memberUserId != null && memberRoleId != null && !organization.hasMember(memberUserId)) {
            organization.addMember(memberUserId, memberRoleId);
            changed = true;
        }
        if (changed) {
            organization = organizationRepository.save(organization);
        }
        return organization;
    }

    private record SeedBatch(List<CatalogItemProjectionSnapshot> catalogs, Organization organization) {
    }
}
