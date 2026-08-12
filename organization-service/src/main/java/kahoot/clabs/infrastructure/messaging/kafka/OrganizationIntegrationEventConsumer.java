package kahoot.clabs.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;
import kahoot.clabs.application.port.write.OrganizationProjectionPort;
import kahoot.clabs.application.readmodel.OrganizationReadModels;

@ApplicationScoped
public class OrganizationIntegrationEventConsumer {

    private static final Logger LOG = Logger.getLogger(OrganizationIntegrationEventConsumer.class);

    private final OrganizationProjectionPort organizationProjectionPort;

    @Inject
    public OrganizationIntegrationEventConsumer(OrganizationProjectionPort organizationProjectionPort) {
        this.organizationProjectionPort = organizationProjectionPort;
    }

    @Incoming("organization-events-in")
    public void consume(OrganizationIntegrationEvent event) {
        if (event == null || event.aggregateId() == null) {
            LOG.warn("Ignoring empty organization integration message");
            return;
        }

        if (OrganizationIntegrationEvent.ORGANIZATION_DELETED.equals(event.eventType())) {
            organizationProjectionPort.deleteById(event.aggregateId());
            LOG.infof(
                    "Projected %s organizationId=%s eventId=%s",
                    event.eventType(),
                    event.aggregateId(),
                    event.eventId());
            return;
        }

        if (event.payload() == null) {
            LOG.warnf("Ignoring %s without payload organizationId=%s", event.eventType(), event.aggregateId());
            return;
        }

        organizationProjectionPort.save(OrganizationReadModels.from(event.payload()));
        LOG.infof(
                "Projected %s organizationId=%s eventId=%s",
                event.eventType(),
                event.aggregateId(),
                event.eventId());
    }
}
