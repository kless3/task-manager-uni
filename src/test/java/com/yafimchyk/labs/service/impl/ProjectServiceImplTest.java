package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.ProjectRequestDto;
import com.yafimchyk.labs.dto.response.ProjectResponseDto;
import com.yafimchyk.labs.exception.DuplicateResourceException;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.ProjectMapper;
import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.model.enums.ProjectStatus;
import com.yafimchyk.labs.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectRequestDto projectRequestDto;
    private ProjectResponseDto projectResponseDto;
    private final Long projectId = 1L;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(projectId);
        project.setTitle("Test Project");
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.ACTIVE);
        project.setStartDate(LocalDateTime.now());
        project.setDeadline(LocalDateTime.now().plusDays(30));

        projectRequestDto = new ProjectRequestDto(
                "Test Project",
                "Test Description",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                ProjectStatus.ACTIVE
        );

        projectResponseDto = new ProjectResponseDto(
                projectId,
                "Test Project",
                "Test Description",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                ProjectStatus.ACTIVE,
                List.of(), // tasks
                List.of()  // meetings
        );
    }

    @Test
    void getAllProjects_Success() {
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        List<ProjectResponseDto> result = projectService.getAllProjects();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(projectId);
    }

    @Test
    void getProjectById_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.getProjectById(projectId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(projectId);
    }

    @Test
    void getProjectById_NotFound_ThrowsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(projectId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found with id: " + projectId);
    }

    @Test
    void createProject_Success() {
        when(projectRepository.existsByTitle(projectRequestDto.title())).thenReturn(false);
        when(projectMapper.toEntity(projectRequestDto)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.createProject(projectRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Test Project");
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void createProject_DuplicateTitle_ThrowsException() {
        when(projectRepository.existsByTitle(projectRequestDto.title())).thenReturn(true);

        assertThatThrownBy(() -> projectService.createProject(projectRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Project already exists!");
    }

    @Test
    void updateProjectById_Success() {
        final ProjectRequestDto updateRequest = new ProjectRequestDto(
                "Updated Project",
                "Updated Description",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                ProjectStatus.ACTIVE
        );

        Project updatedProject = new Project();
        updatedProject.setId(projectId);
        updatedProject.setTitle("Updated Project");

        ProjectResponseDto updatedResponse = new ProjectResponseDto(
                projectId,
                "Updated Project",
                "Updated Description",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                ProjectStatus.ACTIVE,
                List.of(),
                List.of()
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.existsByTitle(updateRequest.title())).thenReturn(false);
        doNothing().when(projectMapper).updateProjectFromDto(updateRequest, project);
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);
        when(projectMapper.toDto(updatedProject)).thenReturn(updatedResponse);

        ProjectResponseDto result = projectService.updateProjectById(projectId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Updated Project");
    }

    @Test
    void updateProjectById_DuplicateTitle_ThrowsException() {
        final ProjectRequestDto updateRequest = new ProjectRequestDto(
                "Duplicate Project",
                "Description",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                ProjectStatus.ACTIVE
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.existsByTitle(updateRequest.title())).thenReturn(true);

        assertThatThrownBy(() -> projectService.updateProjectById(projectId, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Project already exists!");
    }

    @Test
    void deleteProjectById_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(projectRepository).delete(project);

        projectService.deleteProjectById(projectId);

        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    void getFilteredProjects_Success() {
        ProjectStatus status = ProjectStatus.ACTIVE;
        Set<String> labels = Set.of("label1", "label2");
        boolean includedExpired = false;

        when(projectRepository.findProjectsByStatusAndLabels(eq(status), eq(labels), any(LocalDateTime.class)))
                .thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        List<ProjectResponseDto> result = projectService.getFilteredProjects(status, labels, includedExpired);

        assertThat(result).hasSize(1);
    }

    @Test
    void getFilteredProjectsNative_Success() {
        ProjectStatus status = ProjectStatus.ACTIVE;
        Set<String> labels = Set.of("label1", "label2");
        boolean includedExpired = true;

        when(projectRepository.findProjectsByStatusAndLabelsNative(
                eq(status.name()), anyList(), any(LocalDateTime.class)))
                .thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        List<ProjectResponseDto> result = projectService.getFilteredProjectsNative(status, labels, includedExpired);

        assertThat(result).hasSize(1);
    }

    @Test
    void getProjectEntityById_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project result = projectService.getProjectEntityById(projectId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(projectId);
    }
}