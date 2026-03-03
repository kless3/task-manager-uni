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

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
    }

    @Test
    void getTaskEntityById_WhenExists_ShouldReturn() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        var result = taskEntityService.getTaskEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void getTaskEntityById_WhenNotExists_ShouldThrow() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskEntityService.getTaskEntityById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: 99");
    }
}