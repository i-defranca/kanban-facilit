package dev.challenge.api.v1;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(Instant timestamp, String status, T data, String message) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(Instant.now(), "success", data, null);
    }

    public static ApiResponse<?> error(String message) {
        return new ApiResponse<>(Instant.now(), "error", null, message);
    }

    public static ApiResponse<?> error(String message, Map<String, ?> errors) {
        return new ApiResponse<>(Instant.now(), "error", Map.of("errors", errors), message);
    }
}
