package kahoot.clabs.application.usecase;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import kahoot.clabs.application.dto.GameSessionResponse;
import kahoot.clabs.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.application.port.read.GameSessionReadPort;
import kahoot.clabs.application.query.ListGameSessionsQuery;
import kahoot.clabs.domain.valueobject.SessionStatus;
import kahoot.clabs.domain.shared.DomainException;

@ApplicationScoped
public class ListGameSessionsUseCase {

    @Inject
    GameSessionReadPort gameSessionReadPort;

    @Inject
    OrganizationMembershipPort organizationMembershipPort;

    public List<GameSessionResponse> execute(ListGameSessionsQuery query) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, query.organizationId());

        Set<String> statuses = parseStatuses(query.statusCsv());
        return gameSessionReadPort.search(query.organizationId(), statuses, query.quizId()).stream()
                .map(GameSessionResponse::from)
                .toList();
    }

    private Set<String> parseStatuses(String statusCsv) {
        if (statusCsv == null || statusCsv.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(statusCsv.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(this::parseStatus)
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    private SessionStatus parseStatus(String raw) {
        try {
            return SessionStatus.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Invalid session status: " + raw);
        }
    }
}
