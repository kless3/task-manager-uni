package com.yafimchyk.labs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для создания/обновления метки")
public record LabelRequestDto(

        @Schema(
                description = "Название метки",
                example = "BUG",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 10
        )
        @NotBlank(message = "Title is required!")
        @Size(min = 2, max = 10, message = "Title must be between 2 and 10 characters")
        String title
) {
}