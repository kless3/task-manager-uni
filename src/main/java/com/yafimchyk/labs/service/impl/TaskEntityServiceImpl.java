package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.model.Task;
import com.yafimchyk.labs.repository.TaskRepository;
import com.yafimchyk.labs.service.TaskEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskEntityServiceImpl implements TaskEntityService {

    private static final String TASK_NOT_FOUND = "Task not found with id: ";
    private final TaskRepository taskRepository;

    @Override
    @Transactional(readOnly = true)
    public Task getTaskEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND + id));
    }
}
