package dev.challenge.api.v1.dto.request;

import dev.challenge.domain.MemberRole;
import dev.challenge.repository.DepartmentRepository;
import dev.challenge.repository.MemberRepository;
import dev.challenge.validation.annotation.EnumType;
import dev.challenge.validation.annotation.UUIDExists;
import dev.challenge.validation.annotation.Unique;
import jakarta.validation.constraints.Email;

public record MemberUpdateRequest(
    String name,
    @Email @Unique(repository = MemberRepository.class, field = "email") String email,
    @EnumType(MemberRole.class) String role,
    @UUIDExists(DepartmentRepository.class) String department
) {}
