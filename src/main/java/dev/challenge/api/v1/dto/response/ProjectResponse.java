package dev.challenge.api.v1.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Builder
public record ProjectResponse(
    String slug,
    String name,
    String status,
    Integer delayDays,
    BigDecimal remainingTimePercentage,
    LocalDate expectedStart,
    LocalDate expectedEnd,
    LocalDate actualStart,
    LocalDate actualEnd,
    Instant createdAt,
    Instant updatedAt
//    Set<ProjectMemberResponse> members
) {}
