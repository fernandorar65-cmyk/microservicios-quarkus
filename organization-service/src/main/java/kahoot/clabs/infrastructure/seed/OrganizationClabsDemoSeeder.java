package kahoot.clabs.infrastructure.seed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import kahoot.clabs.domain.aggregate.Organization;
import kahoot.clabs.domain.repository.OrganizationRepository;
import kahoot.clabs.domain.valueobject.OrganizationStatus;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationDepartmentJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.entity.OrganizationJobJpaEntity;
import kahoot.clabs.infrastructure.persistence.postgres.repository.OrganizationDepartmentPanacheRepository;
import kahoot.clabs.infrastructure.persistence.postgres.repository.OrganizationJobPanacheRepository;

/**
 * Demo tenant Clabs. Uses SeedIds for members (identity owns user rows; no cross-DB access).
 */
@ApplicationScoped
public class OrganizationClabsDemoSeeder implements DataSeeder {

    private static final Logger LOG = Logger.getLogger(OrganizationClabsDemoSeeder.class);

    private static final String ORG_NAME = "Clabs";
    private static final String ORG_SLUG = "clabs";

    private final OrganizationRepository organizationRepository;
    private final OrganizationDepartmentPanacheRepository departmentRepository;
    private final OrganizationJobPanacheRepository jobRepository;

    @Inject
    public OrganizationClabsDemoSeeder(
            OrganizationRepository organizationRepository,
            OrganizationDepartmentPanacheRepository departmentRepository,
            OrganizationJobPanacheRepository jobRepository) {
        this.organizationRepository = organizationRepository;
        this.departmentRepository = departmentRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public int order() {
        return 30;
    }

    @Override
    public String name() {
        return "organization-clabs-demo";
    }

    @Override
    public void seed() {
        seedDepartments();
        seedJobs();
        seedClabsOrganization();
    }

    private void seedDepartments() {
        List.of(
                new CatalogSeed("Ingeniería de Software", "Desarrollo y mantenimiento de productos"),
                new CatalogSeed("Infraestructura y Cloud", "Servidores, redes y ambientes cloud"),
                new CatalogSeed("QA y Testing", "Calidad, automatización y regresiones"),
                new CatalogSeed("Datos y Analytics", "Pipelines, BI y modelos de datos"),
                new CatalogSeed("Ciberseguridad", "Seguridad, accesos y cumplimiento"),
                new CatalogSeed("Soporte Técnico", "Mesa de ayuda y operaciones IT"),
                new CatalogSeed("Producto Digital", "Discovery, roadmap y UX"),
                new CatalogSeed("DevOps", "CI/CD, observabilidad y releases"))
                .forEach(definition -> {
                    if (departmentRepository.existsByName(definition.name())) {
                        return;
                    }
                    OrganizationDepartmentJpaEntity entity = OrganizationDepartmentJpaEntity.newInstance();
                    entity.setId(UUID.randomUUID());
                    entity.setName(definition.name());
                    entity.setDescription(truncate(definition.description(), 100));
                    departmentRepository.persist(entity);
                });
    }

    private void seedJobs() {
        List.of(
                new CatalogSeed("Software Engineer", "Diseña e implementa features backend/frontend"),
                new CatalogSeed("Tech Lead", "Lidera equipo técnico y estándares de código"),
                new CatalogSeed("DevOps Engineer", "Automatiza despliegues y monitoreo"),
                new CatalogSeed("QA Automation Engineer", "Pruebas automatizadas y calidad continua"),
                new CatalogSeed("Data Engineer", "Construye pipelines y modelos de datos"),
                new CatalogSeed("Security Analyst", "Evalúa riesgos y controles de seguridad"),
                new CatalogSeed("IT Support Specialist", "Resuelve incidencias y soporte a usuarios"),
                new CatalogSeed("Product Manager", "Define prioridades y valor de producto"),
                new CatalogSeed("UX Designer", "Diseña experiencias e interfaces"),
                new CatalogSeed("SRE", "Confiabilidad, SLO y respuesta a incidentes"))
                .forEach(definition -> {
                    if (jobRepository.existsByName(definition.name())) {
                        return;
                    }
                    OrganizationJobJpaEntity entity = OrganizationJobJpaEntity.newInstance();
                    entity.setId(UUID.randomUUID());
                    entity.setName(definition.name());
                    entity.setDescription(truncate(definition.description(), 100));
                    jobRepository.persist(entity);
                });
    }

    private void seedClabsOrganization() {
        if (organizationRepository.findBySlug(ORG_SLUG).isPresent()) {
            LOG.info("Clabs organization already exists — skipping demo org seed");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Organization organization = Organization.rehydrate(
                SeedIds.ORG_CLABS,
                ORG_NAME,
                ORG_SLUG,
                null,
                "Organización demo Clabs enfocada en productos y servicios de tecnología",
                "America/Bogota",
                "es",
                OrganizationStatus.ACTIVE,
                now,
                now,
                List.of());

        organization.addMember(SeedIds.USER_OWNER, SeedIds.ROLE_OWNER);
        organization.addMember(SeedIds.USER_RH, SeedIds.ROLE_RH);
        organization.addMember(SeedIds.USER_MEMBER, SeedIds.ROLE_MEMBER);

        for (String email : demoEmails()) {
            organization.addMember(SeedIds.demoUser(email), SeedIds.ROLE_MEMBER);
        }

        organizationRepository.save(organization);
        LOG.info("Clabs organization seeded with SeedIds members");
    }

    private static List<String> demoEmails() {
        return List.of(
                "valentina.rios@clabs.local",
                "camila.vargas@clabs.local",
                "sofia.mendoza@clabs.local",
                "isabella.castro@clabs.local",
                "mariana.paredes@clabs.local",
                "lucia.herrera@clabs.local",
                "andres.salazar@clabs.local",
                "mateo.guzman@clabs.local",
                "santiago.ortega@clabs.local",
                "diego.navarro@clabs.local",
                "julian.pena@clabs.local",
                "sebastian.rojas@clabs.local");
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max);
    }

    private record CatalogSeed(String name, String description) {
    }
}
