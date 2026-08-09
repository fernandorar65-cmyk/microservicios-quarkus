package kahoot.clabs.quiz.domain.shared;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class AggregateRoot extends AuditableEntity {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
    }

    protected void registerEvent(DomainEvent event) {
        if (event == null) {
            throw new DomainException("Domain event cannot be null");
        }
        domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        if (domainEvents.isEmpty()) {
            return List.of();
        }
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}
