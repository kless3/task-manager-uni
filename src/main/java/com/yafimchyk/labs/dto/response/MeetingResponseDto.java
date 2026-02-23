package com.yafimchyk.labs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO с данными встречи")
public record MeetingResponseDto(

        @Schema(
                description = "ID встречи",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long id,

        @Schema(
                description = "Название встречи",
                example = "Спринт ревью"
        )
        String title,

        @Schema(
                description = "Дата и время встречи",
                example = "2024-03-20T15:00:00"
        )
        LocalDateTime meetingDate,

        @Schema(
                description = "Описание встречи",
                example = "Демонстрация результатов спринта заказчику"
        )
        String description
) {
}