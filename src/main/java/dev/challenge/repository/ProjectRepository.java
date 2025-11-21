package dev.challenge.repository;

import dev.challenge.domain.ProjectStatus;
import dev.challenge.entity.Project;
import dev.challenge.sluggable.SluggableRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends SluggableRepository<Project> {
    List<Project> findAllByStatus(ProjectStatus status);
}
