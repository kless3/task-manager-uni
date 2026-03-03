package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.TaskCreationDto;
import com.yafimchyk.labs.dto.request.TaskRequestDto;
import com.yafimchyk.labs.dto.response.CommentResponseDto;
import com.yafimchyk.labs.dto.response.LabelResponseDto;
import com.yafimchyk.labs.dto.response.TaskResponseDto;
import com.yafimchyk.labs.exception.InitiatedProblemException;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.TaskMapper;
import com.yafimchyk.labs.model.Comment;
import com.yafimchyk.labs.model.Label;
import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.model.Task;
import com.yafimchyk.labs.repository.TaskRepository;
import com.yafimchyk.labs.service.CommentService;
import com.yafimchyk.labs.service.LabelService;
import com.yafimchyk.labs.service.ProjectService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private LabelService labelService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Project project;
    private Task task;
    private TaskRequestDto requestDto;
    private TaskResponseDto responseDto;
    private TaskCreationDto creationDto;
    private Label label;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setProject(project);

        label = new Label();
        label.setId(1L);
        label.setTitle("BUG");

        LabelResponseDto labelResponseDto = new LabelResponseDto(1L, "BUG");
        CommentResponseDto commentResponseDto = new CommentResponseDto(
                1L,
                "Test Comment",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        requestDto = new TaskRequestDto("Test Task", "Test Description");

        creationDto = new TaskCreationDto(
                "Test Task",
                "Test Description",
                "BUG",
                "Test Comment",
                false
        );

        responseDto = new TaskResponseDto(
                1L,
                "Test Task",
                "Test Description",
                List.of(labelResponseDto),
                List.of(commentResponseDto)
        );
    }

    @Test
    void getAllTasksWithGraph_ShouldReturnList() {
        when(taskRepository.findAllWithGraph()).thenReturn(List.of(task));
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        var result = taskService.getAllTasksWithGraph();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().title()).isEqualTo("Test Task");
    }

    @Test
    void getAllTasksWoGraph_ShouldReturnList() {
        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        var result = taskService.getAllTasksWoGraph();

        assertThat(result).hasSize(1);
    }

    @Test
    void getTaskById_WhenExists_ShouldReturn() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        var result = taskService.getTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Test Task");
    }

    @Test
    void getTaskById_WhenNotExists_ShouldThrow() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: 99");
    }

    @Test
    void getTaskByTitle_WhenExists_ShouldReturn() {
        when(taskRepository.findByTitle("Test Task")).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        var result = taskService.getTaskByTitle("Test Task");

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Test Task");
    }

    @Test
    void updateTaskById_ShouldUpdate() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskMapper).updateTaskFromDto(requestDto, task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        var result = taskService.updateTaskById(1L, requestDto);

        assertThat(result).isNotNull();
        verify(taskMapper).updateTaskFromDto(requestDto, task);
    }

    @Test
    void deleteTaskById_ShouldDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTaskById(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void getTasksByProjectId_ShouldReturnList() {
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(task));
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        var result = taskService.getTasksByProjectId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void createTask_ShouldCreateAndReturn() {
        when(projectService.getProjectEntityById(1L)).thenReturn(project);
        when(taskMapper.toEntity(requestDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        var result = taskService.createTask(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(task.getProject()).isEqualTo(project);
        verify(taskRepository).save(task);
    }

    @Test
    void createTaskWithTx_ShouldCreateFullFlow() {
        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setContent("Test Comment");

        when(projectService.getProjectEntityById(1L)).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(labelService.createLabelEntity("BUG")).thenReturn(label);
        when(commentService.createCommentEntity(1L, "Test Comment")).thenReturn(savedComment);
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        var result = taskService.createTaskWithTx(1L, creationDto);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Test Task");
        verify(taskRepository, times(2)).save(any(Task.class));
        verify(labelService).createLabelEntity("BUG");
        verify(commentService).createCommentEntity(1L, "Test Comment");
    }

    @Test
    void createTaskWithTx_WhenInitiateProblem_ShouldThrow() {
        TaskCreationDto problematicDto = new TaskCreationDto(
                "Test Task",
                "Test Description",
                "BUG",
                "Test Comment",
                true
        );

        when(projectService.getProjectEntityById(1L)).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(labelService.createLabelEntity("BUG")).thenReturn(label);

        assertThatThrownBy(() -> taskService.createTaskWithTx(1L, problematicDto))
                .isInstanceOf(InitiatedProblemException.class)
                .hasMessageContaining("Initiated problem was called!");

        verify(commentService, never()).createCommentEntity(any(), any());
    }

    @Test
    void createTaskWoTx_ShouldCreateButWithoutTransaction() {
        when(projectService.getProjectEntityById(1L)).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(labelService.createLabelEntity("BUG")).thenReturn(label);
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        var result = taskService.createTaskWoTx(1L, creationDto);

        assertThat(result).isNotNull();
        verify(taskRepository, times(2)).save(any(Task.class));
    }
}