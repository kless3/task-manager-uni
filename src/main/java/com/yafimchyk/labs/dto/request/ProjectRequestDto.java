package com.yafimchyk.labs.dto.request;

import com.yafimchyk.labs.model.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "DTO для создания/обновления проекта")
public record ProjectRequestDto(

        @Schema(
                description = "Название проекта",
                example = "Разработка мобильного приложения",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 50
        )
        @NotBlank(message = "Title is required!")
        @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
        String title,

        @Schema(
                description = "Описание проекта",
                example = "Мобильное приложение для заказа еды",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Description is required")
        String description,

        @Schema(
                description = "Дата начала проекта",
                example = "2024-01-01T10:00:00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Start date is required")
        LocalDateTime startDate,

        @Schema(
                description = "Дедлайн проекта",
                example = "2024-06-30T18:00:00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Deadline is required")
        LocalDateTime deadline,

        @Schema(
                description = "Статус проекта",
                example = "IN_PROGRESS",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PLANNED", "IN_PROGRESS", "COMPLETED", "ON_HOLD", "CANCELLED"}
        )
        @NotNull(message = "Status is required")
        ProjectStatus status
) {
}