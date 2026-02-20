package com.yafimchyk.labs.service;

import com.yafimchyk.labs.dto.request.ProjectRequestDto;
import com.yafimchyk.labs.dto.response.ProjectResponseDto;
import com.yafimchyk.labs.model.Project;

import java.util.List;

public interface ProjectService {

    List<ProjectResponseDto> getAllProjects();

    ProjectResponseDto getProjectById(Long id);

    ProjectResponseDto createProject(ProjectRequestDto request);

    ProjectResponseDto updateProjectById(Long id, ProjectRequestDto request);

    void deleteProjectById(Long id);

    Project getProjectEntityById(Long id);

}
