package com.yafimchyk.labs.service;

import com.yafimchyk.labs.dto.request.MeetingBulkRequestDto;
import com.yafimchyk.labs.dto.request.MeetingRequestDto;
import com.yafimchyk.labs.dto.response.MeetingResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MeetingService {

    MeetingResponseDto createMeeting(Long projectId, MeetingRequestDto request);

    Page<MeetingResponseDto> getAllMeetings(Pageable pageable);

    MeetingResponseDto getMeetingById(Long id);

    MeetingResponseDto updateMeeting(Long id, MeetingRequestDto meetingRequestDto);

    void deleteMeeting(Long id);

    List<MeetingResponseDto> getMeetingsByProjectId(Long projectId);

    List<MeetingResponseDto> bulkCreateMeetings(Long projectId, MeetingBulkRequestDto bulkRequest);

}
