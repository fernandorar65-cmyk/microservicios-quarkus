package kahoot.clabs.infrastructure.web;

import org.jboss.logging.Logger;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import kahoot.clabs.domain.shared.DomainException;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    private static final Logger LOG = Logger.getLogger(DomainExceptionMapper.class);

    @Override
    public Response toResponse(DomainException exception) {
        int status = exception.getClass().getSimpleName().endsWith("NotFoundException") ? 404 : 400;
        LOG.warnf("Domain error [%d]: %s", status, exception.getMessage());
        return Response.status(status)
                .entity(ApiResponse.error(status, exception.getMessage()))
                .build();
    }
}
