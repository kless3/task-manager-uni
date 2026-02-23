package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.MeetingRequestDto;
import com.yafimchyk.labs.dto.response.MeetingResponseDto;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.MeetingMapper;
import com.yafimchyk.labs.model.Meeting;
import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.repository.MeetingRepository;
import com.yafimchyk.labs.service.MeetingService;
import com.yafimchyk.labs.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private static final String MEETING_NOT_FOUND = "Meeting not found with id: ";

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
}