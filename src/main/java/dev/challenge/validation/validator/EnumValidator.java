package dev.challenge.validation.validator;

import dev.challenge.validation.annotation.EnumType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<EnumType, String> {
    private Set<String> values;

    @Override
    public void initialize(EnumType annotation) {
        this.values = Arrays.stream(annotation.value().getEnumConstants())
                            .map(e -> e.name().toLowerCase())
                            .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.unwrap(HibernateConstraintValidatorContext.class)
               .addMessageParameter("values", String.join(", ", this.values))
               .buildConstraintViolationWithTemplate("{challenge.validation.constraints.EnumType.message}")
               .addConstraintViolation();

        return value == null || this.values.contains(value.toLowerCase());
    }
}
