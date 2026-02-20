package com.yafimchyk.labs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record MeetingRequestDto(

        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        @NotNull(message = "Meeting date is required")
        LocalDateTime meetingDate,

        @Size(max = 200, message = "Notes cannot exceed 200 characters")
        String description
) {

}