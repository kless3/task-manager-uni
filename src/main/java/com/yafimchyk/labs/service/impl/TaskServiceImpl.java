package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.TaskCreationDto;
import com.yafimchyk.labs.dto.request.TaskRequestDto;
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
import com.yafimchyk.labs.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final String TASK_NOT_FOUND = "Task not found with id: ";
    private static final String INITIATED_PROBLEM = "Initiated problem was called!";

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectService projectService;
    private final LabelService labelService;
    private final CommentService commentService;

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAllWithGraph().stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND + id));

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskByTitle(String title) {
        Task task = taskRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND));

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto createTask(Long projectId, TaskRequestDto request) {

        Project projectEntity = projectService.getProjectEntityById(projectId);
        Task task = taskMapper.toEntity(request);

        task.setProject(projectEntity);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTaskById(Long id, TaskRequestDto request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND + id));

        taskMapper.updateTaskFromDto(request, task);

        Task updatedTask = taskRepository.save(task);

        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {

        Task targetTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND + id));

        taskRepository.delete(targetTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    public TaskResponseDto createTaskWoTx(Long projectId, TaskCreationDto request) {

        Project projectEntity = projectService.getProjectEntityById(projectId);

        Task task = new Task();
        task.setProject(projectEntity);
        task.setDescription(request.taskDescription());
        task.setTitle(request.taskTitle());
        Task savedTask = taskRepository.save(task);

        Label labelEntity = labelService.createLabelEntity(request.labelTitle());

        savedTask.getLabels().add(labelEntity);
        labelEntity.getTasks().add(savedTask);
        taskRepository.save(savedTask);

        if (request.initiateProblem()) {
            throw new InitiatedProblemException(INITIATED_PROBLEM);
        }

        commentService.createCommentEntity(savedTask.getId(), request.commentContent());

        return taskMapper.toDto(savedTask);
    }

}
