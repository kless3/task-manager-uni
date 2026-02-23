package com.yafimchyk.labs.controller;

import com.yafimchyk.labs.controller.api.TaskControllerApi;
import com.yafimchyk.labs.dto.request.TaskCreationDto;
import com.yafimchyk.labs.dto.request.TaskRequestDto;
import com.yafimchyk.labs.dto.response.TaskResponseDto;
import com.yafimchyk.labs.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController implements TaskControllerApi {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/byTitle")
    public ResponseEntity<TaskResponseDto> getTaskByTitle(@RequestParam String title) {
        return ResponseEntity.ok(taskService.getTaskByTitle(title));
    }

    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<List<TaskResponseDto>> getTasksByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<TaskResponseDto> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequestDto request) {
        TaskResponseDto createdTask = taskService.createTask(projectId, request);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto request) {
        return ResponseEntity.ok(taskService.updateTaskById(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/project/{projectId}/wotx")
    public ResponseEntity<TaskResponseDto> createTaskWoTx(
            @PathVariable Long projectId,
            @RequestBody TaskCreationDto request) {
        TaskResponseDto createdTask = taskService.createTaskWoTx(projectId, request);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

}