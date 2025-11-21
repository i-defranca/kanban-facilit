package dev.challenge.api.v1.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DepartmentResponse(UUID id, String slug, String name) {
}
