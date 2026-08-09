package kahoot.clabs.infrastructure.rest;

import java.time.Instant;
import java.util.Map;

public record ApiResponse<T>(
        Instant timestamp,
        int status,
        String message,
        T data,
        Map<String, String> errors) {

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(Instant.now(), status, message, data, Map.of());
    }

    public static ApiResponse<Void> error(int status, String message) {
        return new ApiResponse<>(Instant.now(), status, message, null, Map.of());
    }

    public static ApiResponse<Void> validation(Map<String, String> errors) {
        return new ApiResponse<>(
                Instant.now(),
                400,
                "Validation failed",
                null,
                Map.copyOf(errors));
    }
}
