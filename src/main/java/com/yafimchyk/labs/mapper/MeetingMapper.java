package com.yafimchyk.labs.mapper;

import com.yafimchyk.labs.dto.request.MeetingRequestDto;
import com.yafimchyk.labs.dto.response.MeetingResponseDto;
import com.yafimchyk.labs.model.Meeting;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MeetingMapper {

    MeetingResponseDto toDto(Meeting meeting);

    Meeting toEntity(MeetingRequestDto request);

    void updateMeetingFromDto(MeetingRequestDto request, @MappingTarget Meeting meeting);
}
