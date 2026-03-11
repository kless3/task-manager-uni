package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.MeetingRequestDto;
import com.yafimchyk.labs.model.AsyncTask;
import com.yafimchyk.labs.model.Meeting;
import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.model.enums.AsyncTaskStatus;
import com.yafimchyk.labs.repository.MeetingRepository;
import com.yafimchyk.labs.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncMeetingExecutorService {

    private final AsyncTaskStorage asyncTaskStorage;
    private final MeetingRepository meetingRepository;
    private final ProjectService projectService;

    @Async
    public void executeMeetingsCreation(String taskId, Long projectId, List<MeetingRequestDto> meetings) {
        AsyncTask task = asyncTaskStorage.getTask(taskId);

        if (task == null) {
            return;
        }

        task.setStatus(AsyncTaskStatus.IN_PROGRESS);

        try {
            Project project = projectService.getProjectEntityById(projectId);
            int total = meetings.size();

            for (int i = 0; i < total; i++) {
                MeetingRequestDto request = meetings.get(i);
                Meeting meeting = new Meeting();
                meeting.setTitle(request.title());
                meeting.setDescription(request.description());
                meeting.setMeetingDate(request.meetingDate());
                meeting.setProject(project);
                meetingRepository.save(meeting);

                int progress = (i + 1) * 100 / total;
                task.setProgress(progress);
                Thread.sleep(2000);
            }

            task.setStatus(AsyncTaskStatus.COMPLETED);
            task.setEndTime(LocalDateTime.now());
            task.setProgress(100);
            task.setResult("Создано " + total + " встреч");

        } catch (Exception e) {
            task.setStatus(AsyncTaskStatus.FAILED);
            task.setEndTime(LocalDateTime.now());
            task.setResult("Ошибка: " + e.getMessage());
        }
    }
}