package dev.challenge.api.v1.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.challenge.api.v1.ApiResponse;
import dev.challenge.api.v1.dto.request.ProjectCreateRequest;
import dev.challenge.api.v1.dto.request.ProjectStatusUpdateRequest;
import dev.challenge.api.v1.dto.request.ProjectUpdateRequest;
import dev.challenge.api.v1.dto.response.ProjectResponse;
import dev.challenge.domain.ProjectStatus;
import dev.challenge.entity.Project;
import dev.challenge.mapper.ProjectMapper;
import dev.challenge.service.ProjectService;
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
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ObjectMapper objectMapper;
    private final ProjectService service;
    private final ProjectMapper mapper;
    private final Validator validator;

    @GetMapping({"", "/"})
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> index(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(service.list(status).stream().map(mapper::toResponse).toList()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProjectResponse>> show(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(service.find(slug))));
    }

    @PostMapping({"", "/"})
    public ResponseEntity<ApiResponse<ProjectResponse>> store(@Valid @RequestBody ProjectCreateRequest request) {
        Project created = service.create(mapper.toEntity(request));

        return ResponseEntity.created(URI.create("/api/v1/projects/" + created.getSlug()))
                             .body(ApiResponse.success(mapper.toResponse(created)));
    }

    @PatchMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProjectResponse>> update(@PathVariable String slug, @Valid @RequestBody JsonNode patch) {
        ProjectUpdateRequest dto = objectMapper.convertValue(patch, ProjectUpdateRequest.class);
        Set<ConstraintViolation<ProjectUpdateRequest>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(service.update(slug, patch, dto))));
    }

    @PatchMapping("/{slug}/status")
    public ResponseEntity<ApiResponse<ProjectStatus>> updateStatus(@PathVariable String slug, @Valid @RequestBody ProjectStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.updateStatus(slug, request)));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable String slug) {
        service.delete(slug);

        return ResponseEntity.noContent().build();
    }
}
