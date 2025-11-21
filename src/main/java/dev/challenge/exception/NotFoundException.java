package dev.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(Class<?> clazz, String slug) {
        super(String.format("%s not found: %s", clazz.getSimpleName(), slug));
    }
}
