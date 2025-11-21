package dev.challenge.api.v1.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.challenge.api.v1.ApiResponse;
import dev.challenge.api.v1.dto.request.MemberCreateRequest;
import dev.challenge.api.v1.dto.request.MemberUpdateRequest;
import dev.challenge.api.v1.dto.response.MemberResponse;
import dev.challenge.entity.Member;
import dev.challenge.mapper.MemberMapper;
import dev.challenge.service.MemberService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final ObjectMapper objectMapper;
    private final MemberService service;
    private final MemberMapper mapper;
    private final Validator validator;

    @GetMapping({"", "/"})
    public ResponseEntity<ApiResponse<List<MemberResponse>>> index() {
        return ResponseEntity.ok(ApiResponse.success(service.list().stream().map(mapper::toResponse).toList()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<MemberResponse>> show(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(service.find(slug))));
    }

    @PostMapping({"", "/"})
    public ResponseEntity<ApiResponse<MemberResponse>> store(@Valid @RequestBody MemberCreateRequest request) {
        Member created = service.create(mapper.toEntity(request));

        return ResponseEntity.created(URI.create("/api/v1/members/" + created.getSlug()))
                             .body(ApiResponse.success(mapper.toResponse(created)));
    }

    @PatchMapping("/{slug}")
    public ResponseEntity<ApiResponse<MemberResponse>> update(@PathVariable String slug, @Valid @RequestBody JsonNode patch) {
        MemberUpdateRequest dto = objectMapper.convertValue(patch, MemberUpdateRequest.class);
        Set<ConstraintViolation<MemberUpdateRequest>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(service.update(slug, patch, dto))));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable String slug) {
        service.delete(slug);

        return ResponseEntity.noContent().build();
    }
}
