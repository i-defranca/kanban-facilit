package dev.challenge.repository;

import dev.challenge.entity.Department;
import dev.challenge.sluggable.SluggableRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends SluggableRepository<Department> {
}
