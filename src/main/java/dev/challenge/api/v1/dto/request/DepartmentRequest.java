package dev.challenge.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(@NotBlank String name) {
}
