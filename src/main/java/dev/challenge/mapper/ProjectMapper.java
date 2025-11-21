package dev.challenge.mapper;

import dev.challenge.api.v1.dto.request.ProjectCreateRequest;
import dev.challenge.api.v1.dto.request.ProjectUpdateRequest;
import dev.challenge.api.v1.dto.response.ProjectResponse;
import dev.challenge.entity.Project;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    Project toEntity(ProjectCreateRequest request);

    Project toEntity(ProjectUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Project target, ProjectUpdateRequest req);

    @Mapping(target = "status", expression = "java(project.getStatus().getLabel())")
    ProjectResponse toResponse(Project project);
}
