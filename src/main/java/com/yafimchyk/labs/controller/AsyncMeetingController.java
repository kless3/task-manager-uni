package com.yafimchyk.labs.controller;

import com.yafimchyk.labs.dto.request.MeetingBulkRequestDto;
import com.yafimchyk.labs.model.AsyncTask;
import com.yafimchyk.labs.service.impl.MeetingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/meetings/async")
@RequiredArgsConstructor
public class AsyncMeetingController {

    private final MeetingServiceImpl meetingService;

    @PostMapping("/project/{projectId}")
    public ResponseEntity<Map<String, String>> createMeetingsAsync(
            @PathVariable Long projectId,
            @RequestBody MeetingBulkRequestDto request) {

        String taskId = meetingService.createMeetingsAsync(projectId, request);
        Map<String, String> responseBody = Map.of("taskId", taskId);

        return ResponseEntity.accepted().body(responseBody);
    }

    @GetMapping("/status/{taskId}")
    public ResponseEntity<AsyncTask> getTaskStatus(@PathVariable String taskId) {
        AsyncTask task = meetingService.getMeetingTaskStatus(taskId);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<Map<String, AsyncTask>> getAllAsyncTasks() {
        return ResponseEntity.ok(meetingService.getAllAsyncTasks());
    }
}