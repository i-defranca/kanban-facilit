package dev.challenge.unit;

import dev.challenge.entity.Project;
import dev.challenge.exception.NotFoundException;
import dev.challenge.factory.ProjectFactory;
import dev.challenge.repository.ProjectRepository;
import dev.challenge.service.ProjectService;
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
class ProjectUnitTest {

    @InjectMocks
    ProjectService service;

    @Mock
    SlugService<Project> slugger;

    @Mock
    ProjectRepository repository;

    @BeforeEach
    void setup() {
        UnitTestSetup.beforeEach(repository, slugger);
    }

    //    TODO
    //    shouldCreateProjectWithRequiredName
    //    shouldIgnoreRequestStatusAndMetricsOnCreate
    //    shouldUpdateNameWhenPresent
    //    shouldNotUpdateNameWhenAbsent
    //    shouldFailOnInvalidDateRanges
    //    shouldComputeStatusCorrectly
    //    shouldComputeDelayDaysCorrectly
    //    shouldComputeRemainingTimePercentageCorrectly

    @Test
    void shouldCreateProject() {
        Project proj = ProjectFactory.create();
        Project created = service.create(proj);

        assertNotNull(created.getId());
        assertEquals(proj.getName(), created.getName());
        verify(repository).save(any());
    }

    @Test
    void shouldGenerateUniqueSlugs() {
        Project proj = ProjectFactory.create();
        Project created = service.create(proj);
        Project duplicate = service.create(ProjectFactory.create(proj.getName()));

        assertEquals(created.getName(), duplicate.getName());
        assertNotEquals(created.getSlug(), duplicate.getSlug());
        verify(repository, times(2)).save(any());
    }

    @Test
    void shouldFindExistingProject() {
        Project dept = service.create(ProjectFactory.create());

        Project result = service.find(dept.getSlug());

        assertEquals(dept.getId(), result.getId());
        assertEquals(dept.getName(), result.getName());
        verify(repository).findBySlug(dept.getSlug());
    }

    @Test
    void shouldThrowWhenNotFound() {
        assertThrows(NotFoundException.class, () -> service.find("some-slug"));
    }

    @Test
    void shouldListProjects() {
        List<Project> list = List.of(service.create(ProjectFactory.create()), service.create(ProjectFactory.create()));

        assertEquals(list.size(), service.list().size());
        verify(repository).findAll();
    }

    @Test
    void shouldDeleteProject() {
        Project existing = service.create(ProjectFactory.create());
        assertEquals(1, service.list().size());

        service.delete(existing.getSlug());
        assertEquals(0, service.list().size());

        verify(repository).deleteBySlug(existing.getSlug());
    }
}
