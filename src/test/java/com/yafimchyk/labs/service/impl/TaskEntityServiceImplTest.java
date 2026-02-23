package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.model.Task;
import com.yafimchyk.labs.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskEntityServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskEntityServiceImpl taskEntityService;

    private Task task;
    private final Long taskId = 1L;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
    }

    @Test
    void getTaskEntityById_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task result = taskEntityService.getTaskEntityById(taskId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(taskId);
        assertThat(result.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void getTaskEntityById_NotFound_ThrowsException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskEntityService.getTaskEntityById(taskId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: " + taskId);
    }
}