package com.yafimchyk.labs.service;

import com.yafimchyk.labs.dto.request.ProjectRequestDto;
import com.yafimchyk.labs.dto.response.ProjectResponseDto;
import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.model.enums.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectService {

    List<ProjectResponseDto> getProjectsByStatus(ProjectStatus status);

    List<ProjectResponseDto> getAllProjects();

    ProjectResponseDto getProjectById(Long id);

    ProjectResponseDto createProject(ProjectRequestDto request);

    ProjectResponseDto updateProjectById(Long id, ProjectRequestDto request);

    void deleteProjectById(Long id);

    List<ProjectResponseDto> findProjectsByStatusDeadlineAndLabelJpql(
            ProjectStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String labelTitle
    );

    List<ProjectResponseDto> findProjectsByStatusDeadlineAndLabelNative(
            ProjectStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String labelTitle
    );

    Project getProjectEntityById(Long id);

}
