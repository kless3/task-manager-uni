package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.TaskResponseDto;
import com.yafimchyk.labs.exception.TaskException;
import com.yafimchyk.labs.mapper.TaskMapper;
import com.yafimchyk.labs.model.Task;
import com.yafimchyk.labs.repository.TaskRepository;
import com.yafimchyk.labs.service.TaskService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link TaskService}.
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final TaskMapper taskMapper;

  @Override
  public TaskResponseDto getTaskById(Long id) {
    Optional<Task> taskOptional = taskRepository.findById(id);

    if (taskOptional.isEmpty()) {
      throw new TaskException("Task not found");
    }

    return taskMapper.toDto(taskOptional.get());
  }

  @Override
  public TaskResponseDto getTaskByTitle(String title) {
    Optional<Task> taskOptional = taskRepository.findByTitle(title);
    if (taskOptional.isEmpty()) {
      throw new TaskException("Task not found");
    }

    return taskMapper.toDto(taskOptional.get());
  }
}
