package com.yafimchyk.labs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для создания задачи с меткой и комментарием (без транзакции)")
public record TaskCreationDto(

        @Schema(
                description = "Название задачи",
                example = "Реализовать авторизацию",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                minLength = 2,
                maxLength = 50
        )
        @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
        String taskTitle,

        @Schema(
                description = "Описание задачи",
                example = "Добавить JWT аутентификацию и ролевую модель",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Description is required")
        String taskDescription,

        @Schema(
                description = "Название метки",
                example = "FEATURE",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 15
        )
        @NotBlank(message = "Title is required!")
        @Size(min = 2, max = 15, message = "Title must be between 2 and 15 characters")
        String labelTitle,

        @Schema(
                description = "Содержание комментария",
                example = "Нужно использовать Spring Security",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 1000
        )
        @NotBlank(message = "Content is required!")
        @Size(min = 2, max = 1000, message = "Content must be between 2 and 1000 characters")
        String commentContent,

        @Schema(
                description = "Флаг для инициации проблемы с транзакцией",
                example = "false",
                defaultValue = "false"
        )
        boolean initiateProblem
) {
}