package com.yafimchyk.labs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LabelRequestDto(

        @NotBlank(message = "Title is required!")
        @Size(min = 2, max = 10, message = "Title must be between 2 and 10 characters")
        String title
) {
}
