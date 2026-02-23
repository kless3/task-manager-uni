package com.yafimchyk.labs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для создания/обновления комментария")
public record CommentRequestDto(

        @Schema(
                description = "Содержание комментария",
                example = "Отличная работа! Нужно только поправить форматирование.",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 1000
        )
        @NotBlank(message = "Content is required!")
        @Size(min = 2, max = 1000, message = "Content must be between 2 and 1000 characters")
        String content
) {
}