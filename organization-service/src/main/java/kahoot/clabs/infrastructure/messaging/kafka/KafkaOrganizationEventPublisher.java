package kahoot.clabs.infrastructure.messaging.kafka;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.event.CatalogItemUpsertedEvent;
import kahoot.clabs.application.event.OrganizationIntegrationEvent;
import kahoot.clabs.application.port.integration.OrganizationEventPublisher;

@ApplicationScoped
public class KafkaOrganizationEventPublisher implements OrganizationEventPublisher {

    private static final Logger LOG = Logger.getLogger(KafkaOrganizationEventPublisher.class);

    private final Emitter<Record<String, Object>> emitter;

    @Inject
    public KafkaOrganizationEventPublisher(
            @Channel("organization-events-out") Emitter<Record<String, Object>> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void publish(OrganizationIntegrationEvent event) {
        LOG.infof(
                "Publishing %s to organization.events organizationId=%s eventId=%s",
                event.eventType(),
                event.aggregateId(),
                event.eventId());
        emitter.send(Record.of(event.aggregateId().toString(), event));
    }

    @Override
    public void publish(CatalogItemUpsertedEvent event) {
        LOG.infof(
                "Publishing %s kind=%s id=%s eventId=%s",
                event.eventType(),
                event.payload() == null ? "?" : event.payload().catalogKind(),
                event.aggregateId(),
                event.eventId());
        emitter.send(Record.of(event.aggregateId().toString(), event));
    }
}
