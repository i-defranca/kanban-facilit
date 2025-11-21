package dev.challenge.api.v1.dto.request;

import java.time.LocalDate;

public record ProjectUpdateRequest(
    String name,
    LocalDate expectedStart,
    LocalDate expectedEnd,
    LocalDate actualStart,
    LocalDate actualEnd
) {}
