package dev.challenge.mapper;

import dev.challenge.api.v1.dto.request.MemberCreateRequest;
import dev.challenge.api.v1.dto.request.MemberUpdateRequest;
import dev.challenge.api.v1.dto.response.MemberResponse;
import dev.challenge.domain.MemberRole;
import dev.challenge.entity.Department;
import dev.challenge.entity.Member;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member toEntity(MemberCreateRequest request);

    Member toEntity(MemberUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Member target, MemberUpdateRequest req);

    @Mapping(target = "role", expression = "java(member.getRole().getLabel())")
    MemberResponse toResponse(Member member);

    default Department toDepartment(String id) {
        if (id != null) {
            Department d = new Department();
            d.setId(UUID.fromString(id));
            return d;
        }
        return null;
    }

    default MemberRole toRole(String role) {
        if (role != null) {
            return MemberRole.valueOf(role.toUpperCase());
        }
        return null;
    }
}
