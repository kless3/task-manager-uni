package com.yafimchyk.labs.service;

import com.yafimchyk.labs.dto.TaskResponseDto;

/**
 * Service for working with tasks.
 */
public interface TaskService {

  /**
   * Get task by id.
   *
   * @param id task identifier
   * @return task response
   */
  TaskResponseDto getTaskById(Long id);

  /**
   * Get task by title.
   *
   * @param title task title
   * @return task response
   */
  TaskResponseDto getTaskByTitle(String title);
}