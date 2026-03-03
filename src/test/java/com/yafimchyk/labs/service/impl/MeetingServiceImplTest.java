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
import com.yafimchyk.labs.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    @Captor
    private ArgumentCaptor<Meeting> meetingCaptor;

    private Project project;
    private Meeting meeting;
    private MeetingRequestDto requestDto;
    private MeetingResponseDto responseDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        project = new Project();
        project.setId(1L);

        meeting = new Meeting();
        meeting.setId(1L);
        meeting.setTitle("Sprint Planning");
        meeting.setMeetingDate(now);
        meeting.setDescription("Room 101");
        meeting.setProject(project);

        requestDto = new MeetingRequestDto(
                "Sprint Planning",
                now,
                "Room 101"
        );

        responseDto = new MeetingResponseDto(
                1L,
                "Sprint Planning",
                now,
                "Room 101"
        );
    }

    @Test
    void createMeeting_ShouldCreateAndReturn() {
        when(projectService.getProjectEntityById(1L)).thenReturn(project);
        when(meetingMapper.toEntity(requestDto)).thenReturn(meeting);
        when(meetingRepository.save(meeting)).thenReturn(meeting);
        when(meetingMapper.toDto(meeting)).thenReturn(responseDto);

        var result = meetingService.createMeeting(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Sprint Planning");
        assertThat(result.meetingDate()).isEqualTo(now);
        assertThat(result.description()).isEqualTo("Room 101");

        verify(meetingRepository).save(meeting);
        verify(meetingMapper).toEntity(requestDto);
        verify(meetingMapper).toDto(meeting);
    }

    @Test
    void getAllMeetings_ShouldReturnPagedResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Meeting> meetingPage = new PageImpl<>(List.of(meeting), pageable, 1);

        when(meetingRepository.findAll(pageable)).thenReturn(meetingPage);
        when(meetingMapper.toDto(meeting)).thenReturn(responseDto);

        Page<MeetingResponseDto> result = meetingService.getAllMeetings(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().title()).isEqualTo("Sprint Planning");
    }

    @Test
    void getMeetingById_WhenExists_ShouldReturn() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(meetingMapper.toDto(meeting)).thenReturn(responseDto);

        var result = meetingService.getMeetingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getMeetingById_WhenNotExists_ShouldThrow() {
        when(meetingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.getMeetingById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: 99");
    }

    @Test
    void updateMeeting_WhenValid_ShouldUpdate() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        doNothing().when(meetingMapper).updateMeetingFromDto(requestDto, meeting);
        when(meetingRepository.save(meeting)).thenReturn(meeting);
        when(meetingMapper.toDto(meeting)).thenReturn(responseDto);

        var result = meetingService.updateMeeting(1L, requestDto);

        assertThat(result).isNotNull();
        verify(meetingMapper).updateMeetingFromDto(requestDto, meeting);
        verify(meetingRepository).save(meeting);
    }

    @Test
    void deleteMeeting_WhenExists_ShouldDelete() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        doNothing().when(meetingRepository).delete(meeting);

        meetingService.deleteMeeting(1L);

        verify(meetingRepository).delete(meeting);
    }

    @Test
    void getMeetingsByProjectId_ShouldReturnList() {
        when(meetingRepository.findByProjectId(1L)).thenReturn(List.of(meeting));
        when(meetingMapper.toDto(meeting)).thenReturn(responseDto);

        var result = meetingService.getMeetingsByProjectId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().title()).isEqualTo("Sprint Planning");
    }

    @Test
    void bulkCreateWithTx_ShouldCreateAllMeetings() {
        List<MeetingRequestDto> requests = List.of(
                new MeetingRequestDto("Meeting 1", now, "Room 1"),
                new MeetingRequestDto("Meeting 2", now.plusHours(1), "Room 2")
        );

        MeetingBulkRequestDto bulkRequest = new MeetingBulkRequestDto(requests, false);

        Meeting meeting1 = new Meeting();
        meeting1.setId(1L);
        meeting1.setTitle("Meeting 1");

        Meeting meeting2 = new Meeting();
        meeting2.setId(2L);
        meeting2.setTitle("Meeting 2");

        MeetingResponseDto response1 = new MeetingResponseDto(1L, "Meeting 1", now, "Room 1");
        MeetingResponseDto response2 = new MeetingResponseDto(2L, "Meeting 2", now.plusHours(1), "Room 2");

        when(projectService.getProjectEntityById(1L)).thenReturn(project);
        when(meetingMapper.toEntity(requests.get(0))).thenReturn(meeting1);
        when(meetingMapper.toEntity(requests.get(1))).thenReturn(meeting2);
        when(meetingRepository.save(meeting1)).thenReturn(meeting1);
        when(meetingRepository.save(meeting2)).thenReturn(meeting2);
        when(meetingMapper.toDto(meeting1)).thenReturn(response1);
        when(meetingMapper.toDto(meeting2)).thenReturn(response2);

        var result = meetingService.bulkCreateWithTx(1L, bulkRequest);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Meeting 1");
        assertThat(result.get(1).title()).isEqualTo("Meeting 2");

        verify(meetingRepository, times(2)).save(any(Meeting.class));
        verify(meetingMapper, times(2)).toEntity(any(MeetingRequestDto.class));
    }

    @Test
    void bulkCreateMeetings_WhenInitiatedProblem_ShouldThrowAfterFirst() {
        List<MeetingRequestDto> requests = List.of(
                new MeetingRequestDto("Meeting 1", now, "Room 1"),
                new MeetingRequestDto("Meeting 2", now.plusHours(1), "Room 2"),
                new MeetingRequestDto("Meeting 3", now.plusHours(2), "Room 3")
        );

        MeetingBulkRequestDto bulkRequest = new MeetingBulkRequestDto(requests, true);

        Meeting meeting1 = new Meeting();
        meeting1.setId(1L);
        meeting1.setTitle("Meeting 1");

        when(projectService.getProjectEntityById(1L)).thenReturn(project);
        when(meetingMapper.toEntity(requests.getFirst())).thenReturn(meeting1);
        when(meetingRepository.save(meeting1)).thenReturn(meeting1);

        assertThatThrownBy(() -> meetingService.bulkCreateMeetings(1L, bulkRequest))
                .isInstanceOf(InitiatedProblemException.class)
                .hasMessageContaining("Initiated problem was called!");

        verify(meetingRepository, times(1)).save(any(Meeting.class));
    }

    @Test
    void bulkCreateWoTx_ShouldCreateButWithoutTransaction() {
        List<MeetingRequestDto> requests = List.of(
                new MeetingRequestDto("Meeting 1", now, "Room 1")
        );

        MeetingBulkRequestDto bulkRequest = new MeetingBulkRequestDto(requests, false);

        Meeting meeting1 = new Meeting();
        meeting1.setId(1L);
        meeting1.setTitle("Meeting 1");

        MeetingResponseDto response1 = new MeetingResponseDto(1L, "Meeting 1", now, "Room 1");

        when(projectService.getProjectEntityById(1L)).thenReturn(project);
        when(meetingMapper.toEntity(requests.getFirst())).thenReturn(meeting1);
        when(meetingRepository.save(meeting1)).thenReturn(meeting1);
        when(meetingMapper.toDto(meeting1)).thenReturn(response1);

        var result = meetingService.bulkCreateWoTx(1L, bulkRequest);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().title()).isEqualTo("Meeting 1");
    }
}