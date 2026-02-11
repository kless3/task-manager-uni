package com.yafimchyk.labs.controller;

import com.yafimchyk.labs.dto.TaskResponseDto;
import com.yafimchyk.labs.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for task-related operations.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  /**
   * Get task by id.
   *
   * @param id task identifier
   * @return task response
   */
  @GetMapping("/{id}")
  public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
    return ResponseEntity.ok(taskService.getTaskById(id));
  }

  /**
   * Get task by title.
   *
   * @param title task title
   * @return task response
   */
  @GetMapping("/title")
  public ResponseEntity<TaskResponseDto> getTaskByTitle(@RequestParam String title) {
    return ResponseEntity.ok(taskService.getTaskByTitle(title));
  }
}
