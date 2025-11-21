package dev.challenge.entity;

import dev.challenge.sluggable.SlugSource;
import dev.challenge.sluggable.Sluggable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Base implements Sluggable {
    @Id
    @Setter
    @GeneratedValue
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    @Setter
    @SlugSource
    private String name;

    @Setter
    @Column(nullable = false, unique = true)
    private String slug;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Base() {
        super();
    }
}
