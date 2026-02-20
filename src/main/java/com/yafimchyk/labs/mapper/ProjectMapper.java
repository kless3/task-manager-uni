package com.yafimchyk.labs.mapper;

import com.yafimchyk.labs.dto.request.ProjectRequestDto;
import com.yafimchyk.labs.dto.response.ProjectResponseDto;
import com.yafimchyk.labs.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponseDto toDto(Project project);

    Project toEntity(ProjectRequestDto projectRequestDto);

    void updateProjectFromDto(ProjectRequestDto projectRequestDto, @MappingTarget Project project);

}
