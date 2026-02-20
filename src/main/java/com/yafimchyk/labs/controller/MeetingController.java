package com.yafimchyk.labs.controller;

import com.yafimchyk.labs.dto.request.MeetingRequestDto;
import com.yafimchyk.labs.dto.response.MeetingResponseDto;
import com.yafimchyk.labs.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public ResponseEntity<List<MeetingResponseDto>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponseDto> getMeetingById(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getMeetingById(id));
    }

    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<List<MeetingResponseDto>> getMeetingsByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(meetingService.getMeetingsByProjectId(projectId));
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<MeetingResponseDto> createMeeting(
            @PathVariable Long projectId,
            @Valid @RequestBody MeetingRequestDto request) {
        MeetingResponseDto createdMeeting = meetingService.createMeeting(projectId, request);
        return new ResponseEntity<>(createdMeeting, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeetingResponseDto> updateMeeting(
            @PathVariable Long id,
            @Valid @RequestBody MeetingRequestDto request) {
        return ResponseEntity.ok(meetingService.updateMeeting(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }
}