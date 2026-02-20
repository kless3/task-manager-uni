package com.yafimchyk.labs.dto.response;

import java.time.LocalDateTime;

public record MeetingResponseDto(
        Long id,
        String title,
        LocalDateTime meetingDate,
        String description
) {
}