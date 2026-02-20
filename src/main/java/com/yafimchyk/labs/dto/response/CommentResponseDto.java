package com.yafimchyk.labs.dto.response;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        String content,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {
}