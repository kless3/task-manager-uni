package com.yafimchyk.labs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "DTO для массового создания встреч")
public record MeetingBulkRequestDto(

        @Schema(
                description = "Список встреч для создания",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Meetings list cannot be empty")
        @Size(max = 10, message = "Cannot create more than 10 meetings at once")
        @Valid
        List<MeetingRequestDto> meetings
) {
}