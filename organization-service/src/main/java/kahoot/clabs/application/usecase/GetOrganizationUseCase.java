package kahoot.clabs.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import kahoot.clabs.application.dto.OrganizationResponse;
import kahoot.clabs.application.port.out.read.OrganizationReadPort;
import kahoot.clabs.application.query.GetOrganizationQuery;
import kahoot.clabs.domain.exception.OrganizationNotFoundException;

@ApplicationScoped
public class GetOrganizationUseCase {

    @Inject
    OrganizationReadPort organizationReadPort;

    public OrganizationResponse execute(GetOrganizationQuery query) {
        return organizationReadPort.findById(query.organizationId())
                .map(OrganizationResponse::from)
                .orElseThrow(() -> new OrganizationNotFoundException(query.organizationId()));
    }
}
