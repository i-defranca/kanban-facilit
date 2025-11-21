package dev.challenge.api.v1.dto.request;

import dev.challenge.domain.MemberRole;
import dev.challenge.repository.DepartmentRepository;
import dev.challenge.repository.MemberRepository;
import dev.challenge.validation.annotation.EnumType;
import dev.challenge.validation.annotation.UUIDExists;
import dev.challenge.validation.annotation.Unique;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberCreateRequest(
    @NotBlank String name,
    @NotBlank @Email @Unique(repository = MemberRepository.class, field = "email") String email,
    @NotBlank @EnumType(MemberRole.class) String role,
    @NotNull @UUIDExists(DepartmentRepository.class) String department
) {}
