package com.yafimchyk.labs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "DTO для создания/обновления встречи")
public record MeetingRequestDto(

        @Schema(
                description = "Название встречи",
                example = "Планирование спринта",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 3,
                maxLength = 100
        )
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        @Schema(
                description = "Дата и время встречи",
                example = "2024-03-15T14:30:00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Meeting date is required")
        LocalDateTime meetingDate,

        @Schema(
                description = "Описание встречи",
                example = "Обсуждение задач на следующий спринт",
                maxLength = 200,
                nullable = true
        )
        @Size(max = 200, message = "Notes cannot exceed 200 characters")
        String description
) {
}