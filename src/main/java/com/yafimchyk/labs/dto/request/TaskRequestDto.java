package com.yafimchyk.labs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для создания/обновления задачи")
public record TaskRequestDto(

        @Schema(
                description = "Название задачи",
                example = "Разработать REST API",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 50
        )
        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
        String title,

        @Schema(
                description = "Описание задачи",
                example = "Создать эндпоинты для управления пользователями",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Description is required")
        String description
) {
}