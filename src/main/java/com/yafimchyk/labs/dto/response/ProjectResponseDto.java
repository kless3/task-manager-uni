package com.yafimchyk.labs.dto.response;

import com.yafimchyk.labs.model.Task;
import com.yafimchyk.labs.model.enums.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponseDto(
        Long id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime deadline,
        ProjectStatus status,
        List<TaskResponseDto> tasks,
        List<MeetingResponseDto> meetings
) {
}
