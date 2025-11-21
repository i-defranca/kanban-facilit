package dev.challenge.api.v1.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProjectStatusUpdateRequest(@NotNull String status) {
}
