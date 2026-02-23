package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.MeetingBulkRequestDto;
import com.yafimchyk.labs.dto.request.MeetingRequestDto;
import com.yafimchyk.labs.dto.response.MeetingResponseDto;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.MeetingMapper;
import com.yafimchyk.labs.model.Meeting;
import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.repository.MeetingRepository;
import com.yafimchyk.labs.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingServiceImplTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MeetingMapper meetingMapper;

    @InjectMocks
    private MeetingServiceImpl meetingService;

    private Project project;
    private Meeting meeting;
    private MeetingRequestDto meetingRequestDto;
    private MeetingResponseDto meetingResponseDto;
    private final Long meetingId = 1L;
    private final Long projectId = 1L;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(projectId);

        meeting = new Meeting();
        meeting.setId(meetingId);
        meeting.setTitle("Test Meeting");
        meeting.setMeetingDate(LocalDateTime.now());
        meeting.setDescription("Test Description");
        meeting.setProject(project);

        meetingRequestDto = new MeetingRequestDto(
                "Test Meeting",
                LocalDateTime.now(),
                "Test Description"
        );

        meetingResponseDto = new MeetingResponseDto(
                meetingId,
                "Test Meeting",
                LocalDateTime.now(),
                "Test Description"
        );
    }

    @Test
    void createMeeting_Success() {
        when(projectService.getProjectEntityById(projectId)).thenReturn(project);
        when(meetingMapper.toEntity(meetingRequestDto)).thenReturn(meeting);
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        when(meetingMapper.toDto(meeting)).thenReturn(meetingResponseDto);

        MeetingResponseDto result = meetingService.createMeeting(projectId, meetingRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(meetingId);
        assertThat(result.title()).isEqualTo("Test Meeting");
        verify(meetingRepository, times(1)).save(meeting);
    }

    @Test
    void getAllMeetings_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Meeting> meetingPage = new PageImpl<>(List.of(meeting));

        when(meetingRepository.findAll(pageable)).thenReturn(meetingPage);
        when(meetingMapper.toDto(meeting)).thenReturn(meetingResponseDto);

        Page<MeetingResponseDto> result = meetingService.getAllMeetings(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(meetingId);
    }

    @Test
    void getMeetingById_Success() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        when(meetingMapper.toDto(meeting)).thenReturn(meetingResponseDto);

        MeetingResponseDto result = meetingService.getMeetingById(meetingId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(meetingId);
    }

    @Test
    void getMeetingById_NotFound_ThrowsException() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.getMeetingById(meetingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: " + meetingId);
    }

    @Test
    void updateMeeting_Success() {
        final MeetingRequestDto updateRequest = new MeetingRequestDto(
                "Updated Meeting",
                LocalDateTime.now(),
                "Updated Description"
        );

        Meeting updatedMeeting = new Meeting();
        updatedMeeting.setId(meetingId);
        updatedMeeting.setTitle("Updated Meeting");
        updatedMeeting.setDescription("Updated Description");

        MeetingResponseDto updatedResponse = new MeetingResponseDto(
                meetingId,
                "Updated Meeting",
                LocalDateTime.now(),
                "Updated Description"
        );

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        doNothing().when(meetingMapper).updateMeetingFromDto(updateRequest, meeting);
        when(meetingRepository.save(any(Meeting.class))).thenReturn(updatedMeeting);
        when(meetingMapper.toDto(updatedMeeting)).thenReturn(updatedResponse);

        MeetingResponseDto result = meetingService.updateMeeting(meetingId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Updated Meeting");
        assertThat(result.description()).isEqualTo("Updated Description");
    }

    @Test
    void deleteMeeting_Success() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        doNothing().when(meetingRepository).delete(meeting);

        meetingService.deleteMeeting(meetingId);

        verify(meetingRepository, times(1)).delete(meeting);
    }

    @Test
    void getMeetingsByProjectId_Success() {
        when(meetingRepository.findByProjectId(projectId)).thenReturn(List.of(meeting));
        when(meetingMapper.toDto(meeting)).thenReturn(meetingResponseDto);

        List<MeetingResponseDto> result = meetingService.getMeetingsByProjectId(projectId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(meetingId);
    }

    @Test
    void bulkCreateMeetings_Success() {
        List<MeetingRequestDto> requests = List.of(
                new MeetingRequestDto("Meeting 1", LocalDateTime.now(), "Description 1"),
                new MeetingRequestDto("Meeting 2", LocalDateTime.now(), "Description 2")
        );
        MeetingBulkRequestDto bulkRequest = new MeetingBulkRequestDto(requests);

        List<Meeting> meetings = List.of(
                createMeetingWithTitle("Meeting 1", "Description 1"),
                createMeetingWithTitle("Meeting 2", "Description 2")
        );

        List<MeetingResponseDto> responses = List.of(
                new MeetingResponseDto(1L, "Meeting 1", LocalDateTime.now(), "Description 1"),
                new MeetingResponseDto(2L, "Meeting 2", LocalDateTime.now(), "Description 2")
        );

        when(projectService.getProjectEntityById(projectId)).thenReturn(project);
        when(meetingMapper.toEntity(requests.get(0))).thenReturn(meetings.get(0));
        when(meetingMapper.toEntity(requests.get(1))).thenReturn(meetings.get(1));
        when(meetingRepository.saveAll(anyList())).thenReturn(meetings);
        when(meetingMapper.toDto(meetings.get(0))).thenReturn(responses.get(0));
        when(meetingMapper.toDto(meetings.get(1))).thenReturn(responses.get(1));

        List<MeetingResponseDto> result = meetingService.bulkCreateMeetings(projectId, bulkRequest);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Meeting 1");
        assertThat(result.get(1).title()).isEqualTo("Meeting 2");
        verify(meetingRepository, times(1)).saveAll(anyList());
    }

    private Meeting createMeetingWithTitle(String title, String description) {
        Meeting m = new Meeting();
        m.setTitle(title);
        m.setDescription(description);
        m.setProject(project);
        return m;
    }
}