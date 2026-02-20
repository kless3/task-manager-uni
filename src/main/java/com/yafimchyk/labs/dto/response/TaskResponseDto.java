package com.yafimchyk.labs.dto.response;

import java.util.List;

public record TaskResponseDto(
        Long id,
        String title,
        String description,
        List<LabelResponseDto> labels,
        List<CommentResponseDto> comments
) {
}