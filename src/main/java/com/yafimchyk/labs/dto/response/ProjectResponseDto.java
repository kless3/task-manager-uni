package com.yafimchyk.labs.dto.response;

import com.yafimchyk.labs.model.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "DTO с данными проекта")
public record ProjectResponseDto(

        @Schema(
                description = "ID проекта",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long id,

        @Schema(
                description = "Название проекта",
                example = "Интернет-магазин"
        )
        String title,

        @Schema(
                description = "Описание проекта",
                example = "Разработка платформы для онлайн-продаж"
        )
        String description,

        @Schema(
                description = "Дата начала проекта",
                example = "2024-01-15T09:00:00"
        )
        LocalDateTime startDate,

        @Schema(
                description = "Дедлайн проекта",
                example = "2024-05-30T18:00:00"
        )
        LocalDateTime deadline,

        @Schema(
                description = "Статус проекта",
                example = "IN_PROGRESS",
                allowableValues = {"PLANNED", "IN_PROGRESS", "COMPLETED", "ON_HOLD", "CANCELLED"}
        )
        ProjectStatus status,

        @Schema(
                description = "Список задач проекта"
        )
        List<TaskResponseDto> tasks,

        @Schema(
                description = "Список встреч проекта"
        )
        List<MeetingResponseDto> meetings
) {
}