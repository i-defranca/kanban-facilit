package dev.challenge.api.v1.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MemberResponse(
    UUID id,
    String slug,
    String name,
    String role,
    String email,
    DepartmentResponse department
) {
}
