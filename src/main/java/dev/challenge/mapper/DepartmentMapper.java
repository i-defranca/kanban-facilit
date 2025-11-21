package dev.challenge.mapper;

import dev.challenge.api.v1.dto.request.DepartmentRequest;
import dev.challenge.api.v1.dto.response.DepartmentResponse;
import dev.challenge.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toEntity(DepartmentRequest request);

    DepartmentResponse toResponse(Department department);
}
