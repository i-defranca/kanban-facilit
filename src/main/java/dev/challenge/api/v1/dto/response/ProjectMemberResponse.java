package dev.challenge.api.v1.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProjectMemberResponse(
    UUID id,
    String name,
    String role,
    String email,
    String department
) {
}
