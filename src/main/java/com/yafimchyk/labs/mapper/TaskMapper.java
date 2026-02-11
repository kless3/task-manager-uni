package com.yafimchyk.labs.mapper;

import com.yafimchyk.labs.dto.TaskResponseDto;
import com.yafimchyk.labs.model.Task;
import org.mapstruct.Mapper;

/**
 * Mapper for {@link Task} entity and {@link TaskResponseDto}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper {

  TaskResponseDto toDto(Task task);
}