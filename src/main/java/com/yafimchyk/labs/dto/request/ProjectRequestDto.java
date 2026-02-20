package com.yafimchyk.labs.dto.request;

import com.yafimchyk.labs.model.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.time.LocalDateTime;

public record ProjectRequestDto(

        @NotBlank(message = "Title is required!")
        @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Start date is required")
        LocalDateTime startDate,

        @NotNull(message = "Deadline is required")
        LocalDateTime deadline,

        @NotNull(message = "Status is required")
        ProjectStatus status
) {
}
