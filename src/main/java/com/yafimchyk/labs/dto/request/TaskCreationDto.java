package com.yafimchyk.labs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskCreationDto(

        @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
        String taskTitle,

        @NotBlank(message = "Description is required")
        String taskDescription,

        @NotBlank(message = "Title is required!")
        @Size(min = 2, max = 15, message = "Title must be between 2 and 15 characters")
        String labelTitle,

        @NotBlank(message = "Content is required!")
        @Size(min = 2, max = 1000, message = "Content must be between 2 and 1000 characters")
        String commentContent,

        boolean initiateProblem
) {
}
