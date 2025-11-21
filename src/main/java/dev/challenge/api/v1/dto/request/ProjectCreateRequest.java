package dev.challenge.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ProjectCreateRequest(
    @NotBlank String name,
    LocalDate expectedStart,
    LocalDate expectedEnd,
    LocalDate actualStart,
    LocalDate actualEnd
) {}
