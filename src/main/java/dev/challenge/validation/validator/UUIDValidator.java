package dev.challenge.validation.validator;

import dev.challenge.validation.annotation.UUIDExists;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public class UUIDValidator implements ConstraintValidator<UUIDExists, Object> {

    private final Map<String, JpaRepository<?, UUID>> repositories;
    private JpaRepository<?, UUID> repository;
    private String entityName;

    public UUIDValidator(Map<String, JpaRepository<?, UUID>> repositories) {
        this.repositories = repositories;
    }

    @Override
    public void initialize(UUIDExists annotation) {
        Class<?> clazz = annotation.value();

        this.repository = repositories.values()
                                      .stream()
                                      .filter(r -> clazz.isAssignableFrom(r.getClass()))
                                      .findFirst()
                                      .orElseThrow(() -> new IllegalStateException(String.format("Repository type %s not found", clazz.getSimpleName())));
        this.entityName = clazz.getSimpleName().replace("Repository", "");
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value != null) {
            context.disableDefaultConstraintViolation();

            try {
                List<UUID> list = wrap(value);

                context.unwrap(HibernateConstraintValidatorContext.class)
                       .addMessageParameter("entity", this.entityName)
                       .buildConstraintViolationWithTemplate("{challenge.validation.constraints.UUIDExists.message}")
                       .addConstraintViolation();

                return list.isEmpty() || this.repository.findAllById(list).size() == list.size();
            } catch (IllegalArgumentException ex) {
                context.buildConstraintViolationWithTemplate("{jakarta.validation.constraints.UUID.message}")
                       .addConstraintViolation();

                return false;
            }
        }

        return true;
    }

    private List<UUID> wrap(Object value) {
        if (value instanceof UUID id) {
            return List.of(id);
        }
        if (value instanceof Collection<?> col) {
            return col.stream().filter(Objects::nonNull).map(o -> (UUID) o).toList();
        }
        return List.of(UUID.fromString((String) value));
    }
}
