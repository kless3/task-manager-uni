package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.TaskCreationDto;
import com.yafimchyk.labs.dto.request.TaskRequestDto;
import com.yafimchyk.labs.dto.response.CommentResponseDto;
import com.yafimchyk.labs.dto.response.LabelResponseDto;
import com.yafimchyk.labs.dto.response.TaskResponseDto;
import com.yafimchyk.labs.exception.InitiatedProblemException;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.TaskMapper;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
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
    private Label label;
    private TaskRequestDto taskRequestDto;
    private TaskResponseDto taskResponseDto;
    private final Long taskId = 1L;
    private final Long projectId = 1L;
    private final String taskTitle = "Test Task";

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(projectId);

        task = new Task();
        task.setId(taskId);
        task.setTitle(taskTitle);
        task.setDescription("Test Description");
        task.setProject(project);

        label = new Label();
        label.setId(1L);
        label.setTitle("Test Label");

        taskRequestDto = new TaskRequestDto(
                taskTitle,
                "Test Description"
        );

        taskResponseDto = new TaskResponseDto(
                taskId,
                taskTitle,
                "Test Description",
                List.of(),
                List.of()
        );
    }

    @Test
    void getAllTasks_Success() {
        when(taskRepository.findAllWithGraph()).thenReturn(List.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        List<TaskResponseDto> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(taskId);
    }

    @Test
    void getTaskById_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto result = taskService.getTaskById(taskId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(taskId);
    }

    @Test
    void getTaskById_NotFound_ThrowsException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: " + taskId);
    }

    @Test
    void getTaskByTitle_Success() {
        when(taskRepository.findByTitle(taskTitle)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto result = taskService.getTaskByTitle(taskTitle);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo(taskTitle);
    }

    @Test
    void createTask_Success() {
        when(projectService.getProjectEntityById(projectId)).thenReturn(project);
        when(taskMapper.toEntity(taskRequestDto)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto result = taskService.createTask(projectId, taskRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(taskId);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTaskById_Success() {
        final TaskRequestDto updateRequest = new TaskRequestDto(
                "Updated Task",
                "Updated Description"
        );

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");

        TaskResponseDto updatedResponse = new TaskResponseDto(
                taskId,
                "Updated Task",
                "Updated Description",
                List.of(),
                List.of()
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskMapper).updateTaskFromDto(updateRequest, task);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(updatedResponse);

        TaskResponseDto result = taskService.updateTaskById(taskId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Updated Task");
        assertThat(result.description()).isEqualTo("Updated Description");
    }

    @Test
    void deleteTaskById_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTaskById(taskId);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void getTasksByProjectId_Success() {
        when(taskRepository.findByProjectId(projectId)).thenReturn(List.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        List<TaskResponseDto> result = taskService.getTasksByProjectId(projectId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(taskId);
    }

    @Test
    void createTaskWoTx_Success() {
        final TaskCreationDto creationDto = new TaskCreationDto(
                "Test Task",
                "Test Description",
                "Test Label",
                "Test Comment",
                false
        );

        Task savedTask = new Task();
        savedTask.setId(taskId);
        savedTask.setTitle("Test Task");
        savedTask.setDescription("Test Description");
        savedTask.setProject(project);

        LabelResponseDto labelResponse = new LabelResponseDto(1L, "Test Label");
        CommentResponseDto commentResponse = new CommentResponseDto(1L, "Test Comment", null, null);

        TaskResponseDto expectedResponse = new TaskResponseDto(
                taskId,
                "Test Task",
                "Test Description",
                List.of(labelResponse),
                List.of(commentResponse)
        );

        when(projectService.getProjectEntityById(projectId)).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(labelService.createLabelEntity("Test Label")).thenReturn(label);
        when(taskMapper.toDto(any(Task.class))).thenReturn(expectedResponse);

        TaskResponseDto result = taskService.createTaskWoTx(projectId, creationDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(taskId);
        verify(taskRepository, times(2)).save(any(Task.class));
        verify(labelService, times(1)).createLabelEntity("Test Label");
        verify(commentService, times(1)).createCommentEntity(eq(taskId), eq("Test Comment"));
    }

    @Test
    void createTaskWoTx_WithInitiatedProblem_ThrowsException() {
        final TaskCreationDto creationDto = new TaskCreationDto(
                "Test Task",
                "Test Description",
                "Test Label",
                "Test Comment",
                true
        );

        Task savedTask = new Task();
        savedTask.setId(taskId);
        savedTask.setTitle("Test Task");
        savedTask.setDescription("Test Description");
        savedTask.setProject(project);

        when(projectService.getProjectEntityById(projectId)).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(labelService.createLabelEntity("Test Label")).thenReturn(label);

        assertThatThrownBy(() -> taskService.createTaskWoTx(projectId, creationDto))
                .isInstanceOf(InitiatedProblemException.class)
                .hasMessageContaining("Initiated problem was called!");

        verify(taskRepository, times(2)).save(any(Task.class));
        verify(labelService, times(1)).createLabelEntity("Test Label");
        verify(commentService, never()).createCommentEntity(anyLong(), anyString());
    }

    @Test
    void createTaskWithTx_Success() {
        final TaskCreationDto creationDto = new TaskCreationDto(
                "Test Task",
                "Test Description",
                "Test Label",
                "Test Comment",
                false
        );

        Task savedTask = new Task();
        savedTask.setId(taskId);
        savedTask.setTitle("Test Task");
        savedTask.setDescription("Test Description");
        savedTask.setProject(project);

        LabelResponseDto labelResponse = new LabelResponseDto(1L, "Test Label");
        CommentResponseDto commentResponse = new CommentResponseDto(1L, "Test Comment", null, null);

        TaskResponseDto expectedResponse = new TaskResponseDto(
                taskId,
                "Test Task",
                "Test Description",
                List.of(labelResponse),
                List.of(commentResponse)
        );

        when(projectService.getProjectEntityById(projectId)).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(labelService.createLabelEntity("Test Label")).thenReturn(label);
        when(taskMapper.toDto(any(Task.class))).thenReturn(expectedResponse);

        TaskResponseDto result = taskService.createTaskWithTx(projectId, creationDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(taskId);
        verify(taskRepository, times(2)).save(any(Task.class));
        verify(labelService, times(1)).createLabelEntity("Test Label");
        verify(commentService, times(1)).createCommentEntity(eq(taskId), eq("Test Comment"));
    }

    @Test
    void createTaskWithTx_WithInitiatedProblem_ThrowsException() {
        final TaskCreationDto creationDto = new TaskCreationDto(
                "Test Task",
                "Test Description",
                "Test Label",
                "Test Comment",
                true
        );

        Task savedTask = new Task();
        savedTask.setId(taskId);
        savedTask.setTitle("Test Task");
        savedTask.setDescription("Test Description");
        savedTask.setProject(project);

        when(projectService.getProjectEntityById(projectId)).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(labelService.createLabelEntity("Test Label")).thenReturn(label);

        assertThatThrownBy(() -> taskService.createTaskWithTx(projectId, creationDto))
                .isInstanceOf(InitiatedProblemException.class)
                .hasMessageContaining("Initiated problem was called!");

        verify(taskRepository, times(2)).save(any(Task.class));
        verify(labelService, times(1)).createLabelEntity("Test Label");
        verify(commentService, never()).createCommentEntity(anyLong(), anyString());
    }
}