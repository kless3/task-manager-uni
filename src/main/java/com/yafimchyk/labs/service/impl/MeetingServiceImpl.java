package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.MeetingBulkRequestDto;
import com.yafimchyk.labs.dto.request.MeetingRequestDto;
import com.yafimchyk.labs.dto.response.MeetingResponseDto;
import com.yafimchyk.labs.exception.InitiatedProblemException;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.MeetingMapper;
import com.yafimchyk.labs.model.Meeting;
import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.repository.MeetingRepository;
import com.yafimchyk.labs.service.MeetingService;
import com.yafimchyk.labs.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private static final int AMOUNT_OF_SAVED_MEETINGS = 1;
    private static final String MEETING_NOT_FOUND = "Meeting not found with id: ";
    private static final String INITIATED_PROBLEM = "Initiated problem was called!";

    private final ProjectService projectService;
    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;

    @Override
    @Transactional
    public MeetingResponseDto createMeeting(Long projectId, MeetingRequestDto request) {
        Project projectEntity = projectService.getProjectEntityById(projectId);

        Meeting meeting = meetingMapper.toEntity(request);
        meeting.setProject(projectEntity);

        Meeting savedMeeting = meetingRepository.save(meeting);
        return meetingMapper.toDto(savedMeeting);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MeetingResponseDto> getAllMeetings(Pageable pageable) {
        return meetingRepository.findAll(pageable)
                .map(meetingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public MeetingResponseDto getMeetingById(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MEETING_NOT_FOUND + id));
        return meetingMapper.toDto(meeting);
    }

    @Override
    @Transactional
    public MeetingResponseDto updateMeeting(Long id, MeetingRequestDto request) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MEETING_NOT_FOUND + id));

        meetingMapper.updateMeetingFromDto(request, meeting);

        Meeting updatedMeeting = meetingRepository.save(meeting);
        return meetingMapper.toDto(updatedMeeting);
    }

    @Override
    @Transactional
    public void deleteMeeting(Long id) {
        Meeting targetMeeting = meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MEETING_NOT_FOUND + id));

        meetingRepository.delete(targetMeeting);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetingResponseDto> getMeetingsByProjectId(Long projectId) {
        return meetingRepository.findByProjectId(projectId).stream()
                .map(meetingMapper::toDto)
                .toList();
    }

    @Transactional
    public List<MeetingResponseDto> bulkCreateWithTx(Long projectId, MeetingBulkRequestDto bulkRequestDto) {
        return bulkCreateMeetings(projectId, bulkRequestDto);
    }

    public List<MeetingResponseDto> bulkCreateWoTx(Long projectId, MeetingBulkRequestDto bulkRequestDto) {
        return bulkCreateMeetings(projectId, bulkRequestDto);
    }

    public List<MeetingResponseDto> bulkCreateMeetings(Long projectId, MeetingBulkRequestDto bulkRequest) {

        Project projectEntity = projectService.getProjectEntityById(projectId);
        List<MeetingRequestDto> requests = bulkRequest.meetings();

        List<Meeting> meetings = new ArrayList<>();

        int counter = 0;
        for (MeetingRequestDto request : requests) {

            if (bulkRequest.initiatedProblem() && counter == AMOUNT_OF_SAVED_MEETINGS) {
                throw new InitiatedProblemException(INITIATED_PROBLEM);
            }

            Meeting meeting = meetingMapper.toEntity(request);
            meeting.setProject(projectEntity);
            Meeting savedMeeting = meetingRepository.save(meeting);
            meetings.add(savedMeeting);
            counter++;
        }

        return meetings.stream()
                .map(meetingMapper::toDto)
                .toList();
    }
}