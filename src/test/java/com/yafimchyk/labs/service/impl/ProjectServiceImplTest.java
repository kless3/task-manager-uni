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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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
    private ProjectRequestDto requestDto;
    private ProjectResponseDto responseDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        project = new Project();
        project.setId(1L);
        project.setTitle("Test Project");
        project.setDescription("Test Description");
        project.setStartDate(now);
        project.setDeadline(now.plusDays(7));
        project.setStatus(ProjectStatus.ACTIVE);

        requestDto = new ProjectRequestDto(
                "Test Project",
                "Test Description",
                now,
                now.plusDays(7),
                ProjectStatus.ACTIVE
        );

        responseDto = new ProjectResponseDto(
                1L,
                "Test Project",
                "Test Description",
                now,
                now.plusDays(7),
                ProjectStatus.ACTIVE,
                null,
                null
        );
    }

    @Test
    void getAllProjects_ShouldReturnList() {
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        var result = projectService.getAllProjects();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Test Project");
        assertThat(result.get(0).status()).isEqualTo(ProjectStatus.ACTIVE);
    }

    @Test
    void getProjectById_WhenExists_ShouldReturnDto() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        var result = projectService.getProjectById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Test Project");
    }

    @Test
    void getProjectById_WhenNotExists_ShouldThrow() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found with id: 99");
    }

    @Test
    void getProjectsByStatus_ShouldReturnList() {
        when(projectRepository.findByStatus(ProjectStatus.ACTIVE)).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        var result = projectService.getProjectsByStatus(ProjectStatus.ACTIVE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(ProjectStatus.ACTIVE);
    }

    @Test
    void createProject_WhenTitleNotExists_ShouldSave() {
        when(projectRepository.existsByTitle(requestDto.title())).thenReturn(false);
        when(projectMapper.toEntity(requestDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        var result = projectService.createProject(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Test Project");
        verify(projectRepository).save(project);
    }

    @Test
    void createProject_WhenTitleExists_ShouldThrow() {
        when(projectRepository.existsByTitle(requestDto.title())).thenReturn(true);

        assertThatThrownBy(() -> projectService.createProject(requestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Project already exists!");
    }

    @Test
    void updateProjectById_WhenTitleNotChanged_ShouldUpdate() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(projectMapper).updateProjectFromDto(requestDto, project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        var result = projectService.updateProjectById(1L, requestDto);

        assertThat(result).isNotNull();
        verify(projectMapper).updateProjectFromDto(requestDto, project);
        verify(projectRepository, never()).existsByTitle(anyString());
    }

    @Test
    void updateProjectById_WhenTitleChangedAndNotExists_ShouldUpdate() {
        Project existingProject = new Project();
        existingProject.setId(1L);
        existingProject.setTitle("Old Title");
        existingProject.setDescription("Old Description");

        ProjectRequestDto newRequestDto = new ProjectRequestDto(
                "New Title",
                "New Description",
                now,
                now.plusDays(7),
                ProjectStatus.ACTIVE
        );

        ProjectResponseDto newResponseDto = new ProjectResponseDto(
                1L,
                "New Title",
                "New Description",
                now,
                now.plusDays(7),
                ProjectStatus.ACTIVE,
                null,
                null
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(projectRepository.existsByTitle("New Title")).thenReturn(false);
        doNothing().when(projectMapper).updateProjectFromDto(newRequestDto, existingProject);
        when(projectRepository.save(existingProject)).thenReturn(existingProject);
        when(projectMapper.toDto(existingProject)).thenReturn(newResponseDto);

        var result = projectService.updateProjectById(1L, newRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("New Title");
        verify(projectRepository).existsByTitle("New Title");
        verify(projectMapper).updateProjectFromDto(newRequestDto, existingProject);
    }

    @Test
    void updateProjectById_WhenTitleChangedAndExists_ShouldThrow() {
        Project existingProject = new Project();
        existingProject.setId(1L);
        existingProject.setTitle("Old Title");

        ProjectRequestDto newRequestDto = new ProjectRequestDto(
                "Existing Title",
                "Description",
                now,
                now.plusDays(7),
                ProjectStatus.ACTIVE
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(projectRepository.existsByTitle("Existing Title")).thenReturn(true);

        assertThatThrownBy(() -> projectService.updateProjectById(1L, newRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Project already exists!");

        verify(projectRepository, never()).save(any());
        verify(projectMapper, never()).updateProjectFromDto(any(), any());
    }

    @Test
    void deleteProjectById_WhenExists_ShouldDelete() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(projectRepository).delete(project);

        projectService.deleteProjectById(1L);

        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProjectById_WhenNotExists_ShouldThrow() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProjectById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getProjectEntityById_WhenExists_ShouldReturnEntity() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        var result = projectService.getProjectEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findProjectsByStatusDeadlineAndLabelJpql_ShouldReturnList() {
        when(projectRepository.findProjectsByStatusDeadlineAndLabelJpql(
                any(), any(), any(), any())).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        var result = projectService.findProjectsByStatusDeadlineAndLabelJpql(
                ProjectStatus.ACTIVE, now, now.plusDays(7), "label");

        assertThat(result).hasSize(1);
    }

    @Test
    void findProjectsByStatusDeadlineAndLabelNative_ShouldReturnList() {
        when(projectRepository.findProjectsByStatusDeadlineAndLabelNative(
                any(), any(), any(), any())).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        var result = projectService.findProjectsByStatusDeadlineAndLabelNative(
                ProjectStatus.ACTIVE, now, now.plusDays(7), "label");

        assertThat(result).hasSize(1);
    }
}