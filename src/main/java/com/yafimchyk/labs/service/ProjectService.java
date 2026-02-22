package com.yafimchyk.labs.service;

import com.yafimchyk.labs.dto.request.ProjectRequestDto;
import com.yafimchyk.labs.dto.response.ProjectResponseDto;
import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.model.enums.ProjectStatus;

import java.util.List;
import java.util.Set;

public interface ProjectService {

    List<ProjectResponseDto> getAllProjects();

    ProjectResponseDto getProjectById(Long id);

    ProjectResponseDto createProject(ProjectRequestDto request);

    ProjectResponseDto updateProjectById(Long id, ProjectRequestDto request);

    void deleteProjectById(Long id);

    List<ProjectResponseDto> getFilteredProjects(
            ProjectStatus status,
            Set<String> labelTitle,
            boolean includedExpired
    );

    List<ProjectResponseDto> getFilteredProjectsNative(
            ProjectStatus status,
            Set<String> labelTitles,
            boolean includedExpired
    );

    Project getProjectEntityById(Long id);

}
