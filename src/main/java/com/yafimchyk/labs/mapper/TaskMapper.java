package com.yafimchyk.labs.mapper;

import com.yafimchyk.labs.dto.request.TaskRequestDto;
import com.yafimchyk.labs.dto.response.TaskResponseDto;
import com.yafimchyk.labs.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskResponseDto toDto(Task task);

    Task toEntity(TaskRequestDto request);

    void updateTaskFromDto(TaskRequestDto taskRequestDto, @MappingTarget Task task);
}