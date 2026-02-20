package com.yafimchyk.labs.service;

import com.yafimchyk.labs.dto.request.MeetingRequestDto;
import com.yafimchyk.labs.dto.response.MeetingResponseDto;

import java.util.List;

public interface MeetingService {

    MeetingResponseDto createMeeting(Long projectId, MeetingRequestDto request);

    List<MeetingResponseDto> getAllMeetings();

    MeetingResponseDto getMeetingById(Long id);

    MeetingResponseDto updateMeeting(Long id, MeetingRequestDto meetingRequestDto);

    void deleteMeeting(Long id);

    List<MeetingResponseDto> getMeetingsByProjectId(Long projectId);

}
