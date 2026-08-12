package kahoot.clabs.infrastructure.messaging.kafka;

import java.util.UUID;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.CatalogItemProjectionSnapshot;
import kahoot.clabs.application.event.CatalogItemUpsertedEvent;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;
import kahoot.clabs.application.port.write.OrganizationProjectionPort;
import kahoot.clabs.application.readmodel.OrganizationReadModels;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationDepartmentReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationJobReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationMemberStatusReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.document.OrganizationStatusReadDocument;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationDepartmentMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationJobMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationMemberStatusMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.repository.OrganizationStatusMongoRepository;

/**
 * Projects organization read models in Mongo outside the JPA write transaction.
 */
@ApplicationScoped
public class OrganizationIntegrationEventConsumer {

    private static final Logger LOG = Logger.getLogger(OrganizationIntegrationEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final OrganizationProjectionPort organizationProjectionPort;
    private final OrganizationStatusMongoRepository statusMongoRepository;
    private final OrganizationMemberStatusMongoRepository memberStatusMongoRepository;
    private final OrganizationDepartmentMongoRepository departmentMongoRepository;
    private final OrganizationJobMongoRepository jobMongoRepository;

    @Inject
    public OrganizationIntegrationEventConsumer(
            ObjectMapper objectMapper,
            OrganizationProjectionPort organizationProjectionPort,
            OrganizationStatusMongoRepository statusMongoRepository,
            OrganizationMemberStatusMongoRepository memberStatusMongoRepository,
            OrganizationDepartmentMongoRepository departmentMongoRepository,
            OrganizationJobMongoRepository jobMongoRepository) {
        this.objectMapper = objectMapper;
        this.organizationProjectionPort = organizationProjectionPort;
        this.statusMongoRepository = statusMongoRepository;
        this.memberStatusMongoRepository = memberStatusMongoRepository;
        this.departmentMongoRepository = departmentMongoRepository;
        this.jobMongoRepository = jobMongoRepository;
    }

    @Incoming("organization-events-in")
    public void consume(JsonNode event) {
        if (event == null || event.isNull() || !event.hasNonNull("eventType")) {
            LOG.warn("Ignoring empty organization integration message");
            return;
        }

        String eventType = event.get("eventType").asText();
        if (CatalogItemUpsertedEvent.CATALOG_ITEM_UPSERTED.equals(eventType)) {
            projectCatalog(event);
            return;
        }

        OrganizationIntegrationEvent orgEvent =
                objectMapper.convertValue(event, OrganizationIntegrationEvent.class);
        if (orgEvent.aggregateId() == null) {
            LOG.warn("Ignoring organization event without aggregateId");
            return;
        }

        if (OrganizationIntegrationEvent.ORGANIZATION_DELETED.equals(orgEvent.eventType())) {
            organizationProjectionPort.deleteById(orgEvent.aggregateId());
            LOG.infof(
                    "Projected %s organizationId=%s eventId=%s",
                    orgEvent.eventType(),
                    orgEvent.aggregateId(),
                    orgEvent.eventId());
            return;
        }

        if (orgEvent.payload() == null) {
            LOG.warnf("Ignoring %s without payload organizationId=%s", orgEvent.eventType(), orgEvent.aggregateId());
            return;
        }

        organizationProjectionPort.save(OrganizationReadModels.from(orgEvent.payload()));
        LOG.infof(
                "Projected %s organizationId=%s eventId=%s",
                orgEvent.eventType(),
                orgEvent.aggregateId(),
                orgEvent.eventId());
    }

    private void projectCatalog(JsonNode event) {
        CatalogItemUpsertedEvent catalogEvent =
                objectMapper.convertValue(event, CatalogItemUpsertedEvent.class);
        CatalogItemProjectionSnapshot payload = catalogEvent.payload();
        if (payload == null || payload.catalogKind() == null) {
            LOG.warn("Ignoring catalog event without payload");
            return;
        }

        UUID id = payload.id();
        String name = payload.name();
        String description = payload.description();
        switch (payload.catalogKind()) {
            case CatalogItemProjectionSnapshot.KIND_STATUS -> {
                OrganizationStatusReadDocument document = new OrganizationStatusReadDocument();
                document.setId(id);
                document.setName(name);
                document.setDescription(description);
                statusMongoRepository.persistOrUpdate(document);
            }
            case CatalogItemProjectionSnapshot.KIND_MEMBER_STATUS -> {
                OrganizationMemberStatusReadDocument document = new OrganizationMemberStatusReadDocument();
                document.setId(id);
                document.setName(name);
                document.setDescription(description);
                memberStatusMongoRepository.persistOrUpdate(document);
            }
            case CatalogItemProjectionSnapshot.KIND_DEPARTMENT -> {
                OrganizationDepartmentReadDocument document = new OrganizationDepartmentReadDocument();
                document.setId(id);
                document.setName(name);
                document.setDescription(description);
                departmentMongoRepository.persistOrUpdate(document);
            }
            case CatalogItemProjectionSnapshot.KIND_JOB -> {
                OrganizationJobReadDocument document = new OrganizationJobReadDocument();
                document.setId(id);
                document.setName(name);
                document.setDescription(description);
                jobMongoRepository.persistOrUpdate(document);
            }
            default -> {
                LOG.warnf("Unknown catalogKind=%s", payload.catalogKind());
                return;
            }
        }
        LOG.infof(
                "Projected %s kind=%s id=%s eventId=%s",
                catalogEvent.eventType(),
                payload.catalogKind(),
                id,
                catalogEvent.eventId());
    }
}
