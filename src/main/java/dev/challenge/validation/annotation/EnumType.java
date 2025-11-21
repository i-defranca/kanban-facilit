package dev.challenge.validation.annotation;

import dev.challenge.validation.validator.EnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface EnumType {
    Class<? extends Enum<?>> value();

    String message() default "{challenge.validation.constraints.EnumType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
