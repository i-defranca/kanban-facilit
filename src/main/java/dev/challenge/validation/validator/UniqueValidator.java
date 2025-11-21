package dev.challenge.validation.validator;

import dev.challenge.validation.annotation.Unique;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Slf4j
public class UniqueValidator implements ConstraintValidator<Unique, Object> {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private HttpServletRequest request;

    private String slug = "";
    private String field;
    private JpaSpecificationExecutor<?> executor;

    @SneakyThrows
    @Override
    public void initialize(Unique annotation) {
        this.field = annotation.field();
        this.executor = (JpaSpecificationExecutor<?>) context.getBean(annotation.repository());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value != null) {
            context.disableDefaultConstraintViolation();
            log.debug(request.getMethod());
            if (request.getMethod().equalsIgnoreCase("PATCH")) {
                Object slug = ((Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).get("slug");
                if (slug != null) {
                    this.slug = String.valueOf(slug);
                    log.debug("SLUG::::::");
                    log.debug(this.slug);
                }
            }

            try {
                context.unwrap(HibernateConstraintValidatorContext.class)
                       .addMessageParameter("field", this.field)
                       .buildConstraintViolationWithTemplate("{challenge.validation.constraints.Unique.message}")
                       .addConstraintViolation();

                return ! executor.exists((root, query, cb) -> cb.and(cb.equal(root.get(field), value), cb.notEqual(root.get("slug"), this.slug)));
            } catch (IllegalArgumentException ex) {
                context.buildConstraintViolationWithTemplate("{jakarta.validation.constraints.UUID.message}")
                       .addConstraintViolation();

                return false;
            }
        }

        return true;
    }
}
