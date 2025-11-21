package dev.challenge.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.challenge.api.v1.dto.request.MemberUpdateRequest;
import dev.challenge.domain.MemberRole;
import dev.challenge.entity.Department;
import dev.challenge.entity.Member;
import dev.challenge.exception.NotFoundException;
import dev.challenge.exception.RequestValidationException;
import dev.challenge.repository.DepartmentRepository;
import dev.challenge.repository.MemberRepository;
import dev.challenge.sluggable.SlugService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final SlugService<Member> slugify;
    private final MemberRepository repository;
    private final DepartmentRepository departmentRepository;

    public Member create(Member member) {
        if (member.getDepartment() != null) {
            Department department = departmentRepository.findById(member.getDepartment().getId())
                                                        .orElseThrow(() -> new RequestValidationException(Map.of("department", "RelationExists")));
            member.setDepartment(department);
        }
        member.setSlug(slugify.generate(member));
        return repository.save(member);
    }

    public Member find(String slug) {
        return repository.findBySlug(slug).orElseThrow(() -> new NotFoundException(Member.class, slug));
    }

    public List<Member> list() {
        return repository.findAll();
    }

    public long count() {
        return repository.count();
    }

    @Transactional
    public Member update(String slug, JsonNode patch, MemberUpdateRequest dto) {
        Member m = find(slug);
        if (patch.has("name")) {
            if (patch.get("name").asText().isBlank()) {
                throw new RequestValidationException(Map.of("name", "NotBlank"));
            }
            m.setName(dto.name());
            m.setSlug(slugify.generate(m));
        }

        if (patch.has("email")) {
            if (patch.get("email").asText().isBlank()) {
                throw new RequestValidationException(Map.of("email", "NotBlank"));
            }
            m.setEmail(dto.email());
        }
        if (patch.has("role")) {
            if (patch.get("role").asText().isBlank()) {
                throw new RequestValidationException(Map.of("role", "NotBlank"));
            }
            m.setRole(MemberRole.valueOf(dto.role()));
        }
        if (patch.has("department")) {
            if (patch.get("department").asText().isBlank()) {
                throw new RequestValidationException(Map.of("department", "NotBlank"));
            }
            m.setDepartment(departmentRepository.findById(UUID.fromString(dto.department()))
                                                .orElseThrow(() -> new RequestValidationException(Map.of("department", "RelationExists"))));
        }
        return repository.save(m);
    }

    @Transactional
    public void delete(String slug) {
        repository.deleteBySlug(slug);
    }
}
