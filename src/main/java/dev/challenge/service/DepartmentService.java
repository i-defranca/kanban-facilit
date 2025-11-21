package dev.challenge.service;

import dev.challenge.entity.Department;
import dev.challenge.exception.NotFoundException;
import dev.challenge.repository.DepartmentRepository;
import dev.challenge.sluggable.SlugService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;
    private final SlugService<Department> slugify;

    public Department create(Department department) {
        department.setSlug(slugify.generate(department));
        return repository.save(department);
    }

    public Department find(String slug) {
        return repository.findBySlug(slug).orElseThrow(() -> new NotFoundException(Department.class, slug));
    }

    public List<Department> list() {
        return repository.findAll();
    }

    public long count() {
        return repository.count();
    }

    public Department update(String slug, Department data) {
        Department d = find(slug);
        if (!d.getName().equals(data.getName())) {
            d.setName(data.getName());
            d.setSlug(slugify.generate(d));
            d = repository.save(d);
        }
        return d;
    }

    @Transactional
    public void delete(String slug) {
        repository.deleteBySlug(slug);
    }
}
