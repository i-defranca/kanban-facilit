package dev.challenge.sluggable;

import com.github.slugify.Slugify;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
public class SlugService<T extends Sluggable> {
    private final Slugify slugify = Slugify.builder().build();
    private final List<SluggableRepository<T>> repositories;

    public SlugService(List<SluggableRepository<T>> repositories) {
        this.repositories = repositories;
    }

    public String generate(Sluggable entity) {
        String slug = slugify.slugify(fromSource(entity)).toLowerCase();
        String candidate = slug;

        SluggableRepository<T> repository = repositories.stream()
                                                        .filter(r -> Arrays.stream(r.getClass().getInterfaces())
                                                                           .map(Class::getSimpleName)
                                                                           .findFirst()
                                                                           .orElse("Unknown")
                                                                           .equals(entity.getClass()
                                                                                         .getSimpleName() + "Repository"))
                                                        .findFirst()
                                                        .orElseThrow();

        int counter = 0;
        while (repository.existsBySlug(candidate)) {
            counter++;
            candidate = String.format("%s-%s", slug, counter);
        }

        return candidate;
    }

    private String fromSource(Object entity) {
        for (Field f : Stream.of(entity.getClass().getDeclaredFields(), entity.getClass()
                                                                              .getSuperclass()
                                                                              .getDeclaredFields())
                             .flatMap(Stream::of)
                             .toList()) {
            if (f.isAnnotationPresent(SlugSource.class)) {
                f.setAccessible(true);
                try {
                    Object value = f.get(entity);
                    return value != null ? value.toString() : "";
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return "";
    }
}
