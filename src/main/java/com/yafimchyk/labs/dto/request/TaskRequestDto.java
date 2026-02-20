package com.yafimchyk.labs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequestDto(

        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description
) {
}
