package com.yafimchyk.labs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO с данными комментария")
public record CommentResponseDto(

        @Schema(
                description = "ID комментария",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long id,

        @Schema(
                description = "Содержание комментария",
                example = "Отличная работа! Все тесты проходят."
        )
        String content,

        @Schema(
                description = "Дата создания комментария",
                example = "2024-03-10T15:30:00"
        )
        LocalDateTime createdDate,

        @Schema(
                description = "Дата последнего обновления",
                example = "2024-03-11T09:45:00"
        )
        LocalDateTime updatedDate
) {
}