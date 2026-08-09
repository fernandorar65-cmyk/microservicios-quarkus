package kahoot.clabs.infrastructure.rest;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOG = Logger.getLogger(ConstraintViolationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, String> fields = new HashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            fields.put(fieldName(violation), violation.getMessage());
        }
        LOG.warnf("Validation failed: %s", fields);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.validation(fields))
                .build();
    }

    private String fieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int lastDot = path.lastIndexOf('.');
        return lastDot >= 0 ? path.substring(lastDot + 1) : path;
    }
}
