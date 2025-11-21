package dev.challenge.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class RequestValidationException extends RuntimeException {
    @Getter
    private final Map<String, String> errors;

    public RequestValidationException(Map<String, String> errors) {
        this(errors, "Validation failed");
    }

    public RequestValidationException(Map<String, String> errors, String message) {
        super(message);
        Map<String, String> temp = new HashMap<>(errors);
        temp.replaceAll((key, value) -> validationMessage(value));
        this.errors = temp;
    }

    private String validationMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages")
                             .getString(String.format("jakarta.validation.constraints.%s.message", key));
    }
}
