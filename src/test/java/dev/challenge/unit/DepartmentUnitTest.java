package dev.challenge.unit;

import dev.challenge.entity.Department;
import dev.challenge.exception.NotFoundException;
import dev.challenge.factory.DepartmentFactory;
import dev.challenge.repository.DepartmentRepository;
import dev.challenge.service.DepartmentService;
import dev.challenge.sluggable.SlugService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentUnitTest {

    @InjectMocks
    DepartmentService service;

    @Mock
    SlugService<Department> slugger;

    @Mock
    DepartmentRepository repository;

    @BeforeEach
    void setup() {
        UnitTestSetup.beforeEach(repository, slugger);
    }

    @Test
    void shouldCreateDepartment() {
        Department dept = DepartmentFactory.create();
        Department created = service.create(dept);

        assertNotNull(created.getId());
        assertEquals(dept.getName(), created.getName());
        verify(repository).save(any());
    }

    @Test
    void shouldGenerateUniqueSlugs() {
        Department created = service.create(DepartmentFactory.create());
        Department duplicate = service.create(DepartmentFactory.create(created.getName()));

        assertEquals(created.getName(), duplicate.getName());
        assertNotEquals(created.getSlug(), duplicate.getSlug());
        verify(repository, times(2)).save(any());
    }

    @Test
    void shouldFindExistingDepartment() {
        Department dept = service.create(DepartmentFactory.create());

        Department result = service.find(dept.getSlug());

        assertEquals(dept.getId(), result.getId());
        assertEquals(dept.getName(), result.getName());
        verify(repository).findBySlug(dept.getSlug());
    }

    @Test
    void shouldThrowWhenNotFound() {
        assertThrows(NotFoundException.class, () -> service.find("some-slug"));
    }

    @Test
    void shouldListDepartments() {
        List<Department> list = List.of(service.create(DepartmentFactory.create()), service.create(DepartmentFactory.create()));

        assertEquals(list.size(), service.list().size());
        verify(repository).findAll();
    }

    @Test
    void shouldUpdateName() {
        Department existing = service.create(DepartmentFactory.create());
        Department update = DepartmentFactory.create("new name");

        Department result = service.update(existing.getSlug(), update);

        assertEquals(existing.getSlug(), result.getSlug());
        assertEquals(update.getName(), result.getName());
        verify(repository, times(2)).save(any());
    }

    @Test
    void shouldDeleteDepartment() {
        Department existing = service.create(DepartmentFactory.create());
        assertEquals(1, service.list().size());

        service.delete(existing.getSlug());
        assertEquals(0, service.list().size());

        verify(repository).deleteBySlug(existing.getSlug());
    }
}
