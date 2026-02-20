package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.ProjectRequestDto;
import com.yafimchyk.labs.dto.response.ProjectResponseDto;
import com.yafimchyk.labs.exception.DuplicateResourceException;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.ProjectMapper;
import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.repository.ProjectRepository;
import com.yafimchyk.labs.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final String PROJECT_NOT_FOUND = "Project not found with id: ";
    private static final String PROJECT_ALREADY_EXISTS = "Project already exists!";

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;


    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND + id));
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto request) {

        if (projectRepository.existsByTitle(request.title())) {
            throw new DuplicateResourceException(PROJECT_ALREADY_EXISTS);
        }

        Project project = projectMapper.toEntity(request);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    @Override
    @Transactional
    public ProjectResponseDto updateProjectById(Long id, ProjectRequestDto request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND + id));

        if (!project.getTitle().equals(request.title()) && projectRepository.existsByTitle(request.title())) {
            throw new DuplicateResourceException(PROJECT_ALREADY_EXISTS);
        }

        projectMapper.updateProjectFromDto(request, project);

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toDto(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProjectById(Long id) {

        Project targetProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND + id));

        projectRepository.delete(targetProject);
    }

    @Override
    @Transactional(readOnly = true)
    public Project getProjectEntityById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND + id));
    }
}
