package dev.challenge.validation.annotation;

import dev.challenge.validation.validator.UniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueValidator.class)
public @interface Unique {
    String field();

    Class<? extends JpaRepository<?, UUID>> repository();

    String message() default "{challenge.validation.constraints.Unique.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
