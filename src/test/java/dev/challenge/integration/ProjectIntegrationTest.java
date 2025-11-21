package dev.challenge.integration;

import dev.challenge.api.v1.dto.request.ProjectCreateRequest;
import dev.challenge.api.v1.dto.request.ProjectStatusUpdateRequest;
import dev.challenge.api.v1.dto.request.ProjectUpdateRequest;
import dev.challenge.domain.ProjectStatus;
import dev.challenge.entity.Project;
import dev.challenge.exception.NotFoundException;
import dev.challenge.factory.ProjectFactory;
import dev.challenge.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectIntegrationTest extends BaseMvcTest {

    @Autowired
    ProjectService service;

    private final String url = "/api/v1/projects/";

    @Test
    void shouldFilterByStatusOnIndex() throws Exception {
        List<Project> list = List.of(service.create(ProjectFactory.active()), service.create(ProjectFactory.active()));

        mvc.perform(get(this.url).param("status", "active").contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data", hasSize(list.size())));
    }

    @Test
    void shouldReturnRowsOnIndex() throws Exception {
        List<Project> list = List.of(service.create(ProjectFactory.create()), service.create(ProjectFactory.create()));

        mvc.perform(get(this.url).contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data", hasSize(list.size())));
    }

    @Test
    void shouldReturnEntityOnShow() throws Exception {
        service.create(ProjectFactory.create());
        Project proj = service.create(ProjectFactory.create());

        mvc.perform(get(this.url + proj.getSlug()).contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.status").value(proj.getStatus().getLabel()));
    }

    @Test
    void shouldPersistAndReturnOnStore() throws Exception {
        ProjectCreateRequest dto = ProjectFactory.createRequest();

        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.data.name").value(dto.name()));

        assertEquals(1, service.count());
    }

    @Test
    void shouldComputeStatusOnStore() throws Exception {
        ProjectCreateRequest dto = ProjectFactory.createRequest(ProjectFactory.todo());

        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.data.status").value(ProjectStatus.TODO.getLabel()));
    }

    @Test
    void shouldComputeDelayOnStore() throws Exception {
        ProjectCreateRequest dto = ProjectFactory.createRequest(ProjectFactory.late("name", 2));

        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.data.delayDays").value(2));

        assertEquals(1, service.count());
        assertEquals(2, service.find(dto.name()).getDelayDays());
    }

    @Test
    void shouldCreateUniqueSlugsOnStore() throws Exception {
        Project proj = service.create(ProjectFactory.todo());
        ProjectCreateRequest duplicate = ProjectFactory.createRequest(proj.getName());

        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON).content(jsonContent(duplicate)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.data.name").value(proj.getName()))
           .andExpect(jsonPath("$.data.slug", not(proj.getSlug())));

        assertEquals(2, service.count());
    }

    @Test
    void shouldValidateOnStore() throws Exception {
        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON)
                                  .content(jsonContent(ProjectFactory.createRequest(""))))
           .andExpect(status().isUnprocessableEntity())
           .andExpect(jsonPath("$.data.errors").exists());

        assertEquals(0, service.count());
    }

    @Test
    void shouldPersistAndReturnOnUpdate() throws Exception {
        Project proj = service.create(ProjectFactory.create());
        ProjectUpdateRequest dto = ProjectFactory.updateRequest();

        mvc.perform(patch(this.url + proj.getSlug()).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.name").value(dto.name()));

        assertEquals(1, service.count());
        assertEquals(dto.name(), service.find(proj.getSlug()).getName());
    }

    @Test
    void shouldPersistAndReturnOnStatusUpdate() throws Exception {
        Project proj = service.create(ProjectFactory.create());
        ProjectStatusUpdateRequest dto = ProjectFactory.updateStatusRequest();

        mvc.perform(patch(this.url + proj.getSlug() + "/status").contentType(MediaType.APPLICATION_JSON)
                                                                .content(jsonContent(dto)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data").value(dto.status()));

        assertEquals(1, service.count());
        assertEquals(dto.status(), service.find(proj.getSlug()).getStatus().name());
    }

    @Test
    void shouldValidateOnUpdate() throws Exception {
        Project proj = service.create(ProjectFactory.todo());
        mvc.perform(patch(this.url + proj.getSlug()).contentType(MediaType.APPLICATION_JSON)
                                                    .content(jsonContent(ProjectFactory.updateRequest(""))))
           .andExpect(status().isUnprocessableEntity())
           .andExpect(jsonPath("$.data.errors").exists());

        assertEquals(1, service.count());
        assertEquals(service.find(proj.getSlug()).getName(), proj.getName());
    }

    @Test
    void shouldComputeStatusOnUpdate() throws Exception {
        Project proj = service.create(ProjectFactory.create());
        ProjectUpdateRequest dto = ProjectFactory.updateRequest(ProjectFactory.active());

        assertEquals(ProjectStatus.TODO, proj.getStatus());

        mvc.perform(patch(this.url + proj.getSlug()).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.status").value(ProjectStatus.ACTIVE.getLabel()));

        assertEquals(1, service.count());
        assertEquals(ProjectStatus.ACTIVE, service.find(proj.getSlug()).getStatus());
    }

    @Test
    void shouldComputeDelayOnUpdate() throws Exception {
        Project proj = service.create(ProjectFactory.late("slug", 2));
        ProjectUpdateRequest dto = ProjectFactory.updateRequest(ProjectFactory.active());

        assertEquals(2, proj.getDelayDays());

        mvc.perform(patch(this.url + proj.getSlug()).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.delayDays").value(0));

        assertEquals(1, service.count());
        assertEquals(0, service.find(proj.getSlug()).getDelayDays());
    }

    @Test
    void shouldClearActualStartOnUpdate() throws Exception {
        Project proj = service.create(ProjectFactory.active());
        ProjectUpdateRequest dto = new ProjectUpdateRequest(proj.getName(), proj.getExpectedStart(), proj.getExpectedEnd(), null, proj.getActualEnd());

        mvc.perform(patch(this.url + proj.getSlug()).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.actualStart", nullValue()));

        assertNull(service.find(proj.getSlug()).getActualStart());
    }

    @Test
    void shouldComputeRemainingTimeOnUpdate() throws Exception {
        Project proj = service.create(ProjectFactory.create());
        ProjectUpdateRequest dto = ProjectFactory.updateRequest(ProjectFactory.active());
        // expectedStart -5
        // expectedEnd   +5
        // remaining = 50%

        assertEquals(BigDecimal.ZERO, proj.getRemainingTimePercentage());

        mvc.perform(patch(this.url + proj.getSlug()).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.remainingTimePercentage").value("50.0"));

        assertEquals(1, service.count());
        assertEquals(BigDecimal.valueOf(50).setScale(1, RoundingMode.HALF_UP), service.find(proj.getSlug())
                                                                                      .getRemainingTimePercentage());
    }

    @Test
    void shouldUpdateSlugsOnNameUpdate() throws Exception {
        Project proj = service.create(ProjectFactory.todo());
        String previous = proj.getSlug();

        mvc.perform(patch(this.url + proj.getSlug()).contentType(MediaType.APPLICATION_JSON)
                                                    .content(jsonContent(ProjectFactory.updateRequest())))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.slug", not(previous)));

        assertEquals(1, service.count());
    }

    @Test
    void shouldRemoveAndReturnOnDelete() throws Exception {
        Project proj = service.create(ProjectFactory.todo());

        mvc.perform(delete(this.url + proj.getSlug()))
           .andExpect(status().isNoContent())
           .andExpect(jsonPath("$.data.errors").doesNotExist());

        assertThatThrownBy(() -> service.find(proj.getSlug())).isInstanceOf(NotFoundException.class);
    }
}
