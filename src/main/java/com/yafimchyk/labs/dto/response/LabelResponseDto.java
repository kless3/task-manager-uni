package com.yafimchyk.labs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO с данными метки")
public record LabelResponseDto(

        @Schema(
                description = "ID метки",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long id,

        @Schema(
                description = "Название метки",
                example = "BUG"
        )
        String title
) {
}