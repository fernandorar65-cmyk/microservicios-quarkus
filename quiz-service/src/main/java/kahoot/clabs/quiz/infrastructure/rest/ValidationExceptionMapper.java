package kahoot.clabs.quiz.infrastructure.rest;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger log = Logger.getLogger(ValidationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, String> fields = new HashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            fields.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        log.warnf("Validation failed: %s", fields);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.validation(fields))
                .build();
    }
}
