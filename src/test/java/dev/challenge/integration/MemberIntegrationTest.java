package dev.challenge.integration;

import dev.challenge.api.v1.dto.request.MemberCreateRequest;
import dev.challenge.api.v1.dto.request.MemberUpdateRequest;
import dev.challenge.entity.Member;
import dev.challenge.exception.NotFoundException;
import dev.challenge.factory.MemberFactory;
import dev.challenge.mapper.MemberMapper;
import dev.challenge.service.DepartmentService;
import dev.challenge.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberIntegrationTest extends BaseMvcTest {

    @Autowired
    MemberService service;

    @Autowired
    DepartmentService deptService;

    @Autowired
    MemberMapper mapper;

    private final String url = "/api/v1/members/";

    @Test
    void shouldReturnRowsOnIndex() throws Exception {
        List<Member> list = List.of(service.create(MemberFactory.create()), service.create(MemberFactory.create()));

        mvc.perform(get(this.url).contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data", hasSize(list.size())));
    }

    @Test
    void shouldReturnEntityOnShow() throws Exception {
        service.create(MemberFactory.create(deptService));
        Member mem = service.create(MemberFactory.create(deptService));

        mvc.perform(get(this.url + mem.getSlug()).contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.email").value(mem.getEmail()));
    }

    @Test
    void shouldPersistAndReturnOnStore() throws Exception {
        MemberCreateRequest dto = MemberFactory.createRequest(deptService);

        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.data.name").value(dto.name()));

        assertEquals(1, service.count());
    }

    @Test
    void shouldCreateUniqueSlugsOnStore() throws Exception {
        Member member = service.create(mapper.toEntity(MemberFactory.createRequest(deptService)));
        MemberCreateRequest duplicate = MemberFactory.createRequest(member.getName(), deptService);

        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON).content(jsonContent(duplicate)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.data.name").value(member.getName()))
           .andExpect(jsonPath("$.data.slug", not(member.getSlug())));

        assertEquals(2, service.count());
    }

    @Test
    void shouldStoreDepartment() throws Exception {
        MemberCreateRequest dto = MemberFactory.createRequest(deptService);

        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON).content(jsonContent(dto)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.data.department.id").value(dto.department()));

        assertEquals(1, service.count());
    }

    @Test
    void shouldValidateOnStore() throws Exception {
        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON)
                                  .content(jsonContent(MemberFactory.createRequest("", deptService))))
           .andExpect(status().isUnprocessableEntity())
           .andExpect(jsonPath("$.data.errors.name").exists());

        assertEquals(0, service.count());
    }

    @Test
    void shouldValidateUniqueEmailOnStore() throws Exception {
        Member member = service.create(MemberFactory.create(deptService));

        mvc.perform(post(this.url).contentType(MediaType.APPLICATION_JSON)
                                  .content(jsonContent(MemberFactory.createEmailRequest(member.getEmail(), deptService))))
           .andExpect(status().isUnprocessableEntity())
           .andExpect(jsonPath("$.data.errors.email").exists());

        assertEquals(1, service.count());
    }

    @Test
    void shouldPersistAndReturnOnUpdate() throws Exception {
        Member member = service.create(mapper.toEntity(MemberFactory.updateRequest(deptService)));
        MemberUpdateRequest dto = MemberFactory.updateRequest(deptService);

        mvc.perform(patch(this.url + member.getSlug()).contentType(MediaType.APPLICATION_JSON)
                                                      .content(jsonContent(dto)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.name").value(dto.name()));

        assertEquals(1, service.count());
        assertEquals(dto.name(), service.find(member.getSlug()).getName());
    }

    @Test
    void shouldValidateOnUpdate() throws Exception {
        Member member = service.create(mapper.toEntity(MemberFactory.updateRequest(deptService)));
        mvc.perform(patch(this.url + member.getSlug()).contentType(MediaType.APPLICATION_JSON)
                                                      .content(jsonContent(MemberFactory.updateRequest("", deptService))))
           .andExpect(status().isUnprocessableEntity())
           .andExpect(jsonPath("$.data.errors").exists());

        assertEquals(1, service.count());
        assertEquals(service.find(member.getSlug()).getName(), member.getName());
    }

    @Test
    void shouldUpdateSlugsOnNameUpdate() throws Exception {
        Member member = service.create(mapper.toEntity(MemberFactory.updateRequest(deptService)));
        String previous = member.getSlug();

        mvc.perform(patch(this.url + member.getSlug()).contentType(MediaType.APPLICATION_JSON)
                                                      .content(jsonContent(MemberFactory.updateRequest(deptService))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.slug", not(previous)));

        assertEquals(1, service.count());
    }

    @Test
    void shouldValidateUniqueEmailOnUpdate() throws Exception {
        Member other = service.create(mapper.toEntity(MemberFactory.updateRequest(deptService)));
        Member member = service.create(mapper.toEntity(MemberFactory.updateRequest(deptService)));

        mvc.perform(patch(this.url + member.getSlug()).contentType(MediaType.APPLICATION_JSON)
                                                      .content(jsonContent(MemberFactory.createEmailRequest(other.getEmail(), deptService))))
           .andExpect(status().isUnprocessableEntity())
           .andExpect(jsonPath("$.data.errors.email").exists());

        assertNotEquals(other.getEmail(), service.find(member.getSlug()).getEmail());
    }

    @Test
    void shouldAllowSameEmailOnUpdate() throws Exception {
        Member member = service.create(mapper.toEntity(MemberFactory.updateRequest(deptService)));

        mvc.perform(patch(this.url + member.getSlug()).contentType(MediaType.APPLICATION_JSON)
                                                      .content(jsonContent(MemberFactory.createEmailRequest(member.getEmail(), deptService))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.errors.email").doesNotExist());
    }

    @Test
    void shouldUpdateDepartment() throws Exception {
        Member member = service.create(mapper.toEntity(MemberFactory.updateRequest(deptService)));
        MemberUpdateRequest dto = MemberFactory.updateRequest(deptService);

        mvc.perform(patch(this.url + member.getSlug()).contentType(MediaType.APPLICATION_JSON)
                                                      .content(jsonContent(dto)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.department.id").value(dto.department()));

        assertEquals(1, service.count());
        assertEquals(dto.department(), service.find(member.getSlug()).getDepartment().getId().toString());
    }

    @Test
    void shouldRemoveAndReturnOnDelete() throws Exception {
        Member member = service.create(mapper.toEntity(MemberFactory.createRequest(deptService)));

        mvc.perform(delete(this.url + member.getSlug()))
           .andExpect(status().isNoContent())
           .andExpect(jsonPath("$.data.errors").doesNotExist());

        assertThatThrownBy(() -> service.find(member.getSlug())).isInstanceOf(NotFoundException.class);
    }
}
