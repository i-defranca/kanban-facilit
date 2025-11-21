package dev.challenge.api.v1.rest;

import dev.challenge.api.v1.ApiResponse;
import dev.challenge.api.v1.dto.request.DepartmentRequest;
import dev.challenge.api.v1.dto.response.DepartmentResponse;
import dev.challenge.entity.Department;
import dev.challenge.mapper.DepartmentMapper;
import dev.challenge.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;
    private final DepartmentMapper mapper;

    @GetMapping({"", "/"})
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> index() {
        return ResponseEntity.ok(ApiResponse.success(service.list().stream().map(mapper::toResponse).toList()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> show(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(service.find(slug))));
    }

    @PostMapping({"", "/"})
    public ResponseEntity<ApiResponse<DepartmentResponse>> store(@Valid @RequestBody DepartmentRequest request) {
        Department created = service.create(mapper.toEntity(request));

        return ResponseEntity.created(URI.create("/api/v1/departments/" + created.getSlug()))
                             .body(ApiResponse.success(mapper.toResponse(created)));
    }

    @PatchMapping("/{slug}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(@PathVariable String slug, @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(service.update(slug, mapper.toEntity(request)))));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable String slug) {
        service.delete(slug);

        return ResponseEntity.noContent().build();
    }
}
