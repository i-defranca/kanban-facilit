package dev.challenge.exception;

import dev.challenge.api.v1.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleRequestValidation(RequestValidationException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                             .body(ApiResponse.error(ex.getMessage(), ex.getErrors()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            String field = v.getPropertyPath().toString();
            String message = v.getMessage();
            errors.put(field, message);
        }

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ApiResponse.error(ex.getMessage(), errors));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = ex.getBindingResult()
                                             .getFieldErrors()
                                             .stream()
                                             .collect(Collectors.toMap(FieldError::getField, fe -> {
                                                 if (fe.getDefaultMessage() != null) {
                                                     return new ArrayList<>(List.of(fe.getDefaultMessage()));
                                                 }
                                                 return new ArrayList<>();
                                             }, (existing, replacement) -> {
                                                 existing.addAll(replacement);
                                                 return existing;
                                             }));

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                             .body(ApiResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ex.getMessage()));
    }
}

//200 OK: The request was successful.
//201 Created: A new resource was successfully created.
//204 No Content: The request was successful, but there’s no content to return.
//400 Bad Request: The request was malformed or invalid.
//404 Not Found: The requested resource does not exist.
//500 Internal Server Error: An unexpected error occurred on the server.
