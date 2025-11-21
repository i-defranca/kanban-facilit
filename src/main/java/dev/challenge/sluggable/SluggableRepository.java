package dev.challenge.sluggable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface SluggableRepository<T extends Sluggable> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
    Optional<T> findBySlug(String slug);

    boolean existsBySlug(String slug);

    void deleteBySlug(String slug);
}
