package com.yafimchyk.labs.service;

import com.yafimchyk.labs.dto.request.TaskCreationDto;
import com.yafimchyk.labs.dto.request.TaskRequestDto;
import com.yafimchyk.labs.dto.response.TaskResponseDto;
import com.yafimchyk.labs.model.Task;

import java.util.List;

public interface TaskService {

    List<TaskResponseDto> getAllTasks();

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto getTaskByTitle(String title);

    TaskResponseDto createTask(Long projectId, TaskRequestDto request);

    TaskResponseDto updateTaskById(Long id, TaskRequestDto request);

    void deleteTaskById(Long id);

    List<TaskResponseDto> getTasksByProjectId(Long projectId);

    TaskResponseDto createTaskWoTx(Long projectId, TaskCreationDto request);
}