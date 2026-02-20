package com.yafimchyk.labs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequestDto(
        @NotBlank(message = "Content is required!")
        @Size(min = 2, max = 1000, message = "Content must be between 2 and 1000 characters")
        String content
) {
}
