package com.yafimchyk.labs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for task response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

  private String title;
  private String description;
}

 