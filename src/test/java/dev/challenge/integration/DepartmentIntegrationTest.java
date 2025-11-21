package dev.challenge.integration;

import dev.challenge.api.v1.dto.request.DepartmentRequest;
import dev.challenge.entity.Department;
import dev.challenge.exception.NotFoundException;
import dev.challenge.factory.DepartmentFactory;
import dev.challenge.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DepartmentIntegrationTest extends BaseMvcTest {

    @Autowired
    DepartmentService service;

    private final String url = "/api/v1/departments/";

    @Test
    void shouldReturnRowsOnIndex() throws Exception {
        List<Department> list = List.of(service.create(DepartmentFactory.create()), service.create(DepartmentFactory.create()));

        mvc.perform(get(this.url).contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data", hasSize(list.size())));
    }

    @Test
    void shouldReturnEntityOnShow() throws Exception {
        service.create(DepartmentFactory.create());
        Department dept = service.create(DepartmentFactory.create());

        mvc.perform(get(this.url + dept.getSlug()).contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.name").value(dept.getName()));
    }

    @Test
    void shouldPersistAndReturnOnStore() throws Exception {
        DepartmentRequest dto = DepartmentFactory.request();

        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.data.name").value(dto.name()));

        assertEquals(1, service.count());
    }

    @Test
    void shouldCreateUniqueSlugsOnStore() throws Exception {
        Department dept = service.create(DepartmentFactory.create());
        DepartmentRequest duplicate = DepartmentFactory.request(dept.getName());

        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON).content(jsonContent(duplicate)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.data.name").value(dept.getName()))
           .andExpect(jsonPath("$.data.slug", not(dept.getSlug())));

        assertEquals(2, service.count());
    }

    @Test
    void shouldValidateOnStore() throws Exception {
        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON)
                                  .content(jsonContent(DepartmentFactory.request(""))))
           .andExpect(status().isUnprocessableEntity())
           .andExpect(jsonPath("$.data.errors").exists());

        assertEquals(0, service.count());
    }

    @Test
    void shouldPersistAndReturnOnUpdate() throws Exception {
        Department dept = service.create(DepartmentFactory.create());
        DepartmentRequest dto = DepartmentFactory.request();

        mvc.perform(patch(this.url + dept.getSlug()).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.name").value(dto.name()));

        assertEquals(1, service.count());
        assertEquals(dto.name(), service.find(dept.getSlug()).getName());
    }

    @Test
    void shouldValidateOnUpdate() throws Exception {
        Department dept = service.create(DepartmentFactory.create());
        mvc.perform(patch(this.url + dept.getSlug()).contentType(MediaType.APPLICATION_JSON)
                                                    .content(jsonContent(DepartmentFactory.request(""))))
           .andExpect(status().isUnprocessableEntity())
           .andExpect(jsonPath("$.data.errors").exists());

        assertEquals(1, service.count());
        assertEquals(service.find(dept.getSlug()).getName(), dept.getName());
    }

    @Test
    void shouldUpdateSlugsOnNameUpdate() throws Exception {
        Department dept = service.create(DepartmentFactory.create());
        String previous = dept.getSlug();

        mvc.perform(patch(this.url + dept.getSlug()).contentType(MediaType.APPLICATION_JSON)
                                                    .content(jsonContent(DepartmentFactory.request("new name"))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.slug", not(previous)));

        assertEquals(1, service.count());
    }

    @Test
    void shouldRemoveAndReturnOnDelete() throws Exception {
        Department dept = service.create(DepartmentFactory.create());

        mvc.perform(delete(this.url + dept.getSlug()))
           .andExpect(status().isNoContent())
           .andExpect(jsonPath("$.data.errors").doesNotExist());

        assertThatThrownBy(() -> service.find(dept.getSlug())).isInstanceOf(NotFoundException.class);
    }
}
