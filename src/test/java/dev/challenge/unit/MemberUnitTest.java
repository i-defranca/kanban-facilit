package dev.challenge.unit;

import dev.challenge.entity.Member;
import dev.challenge.exception.NotFoundException;
import dev.challenge.factory.MemberFactory;
import dev.challenge.repository.MemberRepository;
import dev.challenge.service.MemberService;
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
class MemberUnitTest {

    @InjectMocks
    MemberService service;

    @Mock
    SlugService<Member> slugger;

    @Mock
    MemberRepository repository;

    @BeforeEach
    void setup() {
        UnitTestSetup.beforeEach(repository, slugger);
    }

    @Test
    void shouldCreateMember() {
        Member mem = MemberFactory.create();
        Member created = service.create(mem);

        assertNotNull(created.getId());
        assertEquals(mem.getName(), created.getName());
        verify(repository).save(any());
    }

    @Test
    void shouldGenerateUniqueSlugs() {
        Member mem = MemberFactory.create();
        Member created = service.create(mem);
        Member duplicate = service.create(MemberFactory.create(mem.getName()));

        assertEquals(created.getName(), duplicate.getName());
        assertNotEquals(created.getSlug(), duplicate.getSlug());
        verify(repository, times(2)).save(any());
    }

    @Test
    void shouldFindExistingMember() {
        Member mem = service.create(MemberFactory.create());

        Member result = service.find(mem.getSlug());

        assertEquals(mem.getId(), result.getId());
        assertEquals(mem.getName(), result.getName());
        verify(repository).findBySlug(mem.getSlug());
    }

    @Test
    void shouldThrowWhenNotFound() {
        assertThrows(NotFoundException.class, () -> service.find("some-slug"));
    }

    @Test
    void shouldListMembers() {
        List<Member> list = List.of(service.create(MemberFactory.create()), service.create(MemberFactory.create()));

        assertEquals(list.size(), service.list().size());
        verify(repository).findAll();
    }

    @Test
    void shouldDeleteMember() {
        Member existing = service.create(MemberFactory.create());
        assertEquals(1, service.list().size());

        service.delete(existing.getSlug());
        assertEquals(0, service.list().size());

        verify(repository).deleteBySlug(existing.getSlug());
    }
}
