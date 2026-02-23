package com.yafimchyk.labs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO с данными задачи")
public record TaskResponseDto(

        @Schema(
                description = "ID задачи",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long id,

        @Schema(
                description = "Название задачи",
                example = "Реализовать регистрацию пользователей"
        )
        String title,

        @Schema(
                description = "Описание задачи",
                example = "Добавить форму регистрации и валидацию email"
        )
        String description,

        @Schema(
                description = "Список меток задачи",
                example = "[{\"id\": 1, \"title\": \"FEATURE\"}]"
        )
        List<LabelResponseDto> labels,

        @Schema(
                description = "Список комментариев к задаче",
                example = "[{\"id\": 1, \"content\": \"Нужно использовать BCrypt\"}]"
        )
        List<CommentResponseDto> comments
) {
}