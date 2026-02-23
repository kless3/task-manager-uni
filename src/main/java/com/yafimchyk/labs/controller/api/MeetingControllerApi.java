package com.yafimchyk.labs.controller.api;

import com.yafimchyk.labs.dto.request.MeetingRequestDto;
import com.yafimchyk.labs.dto.response.MeetingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Meeting Controller", description = "Управление встречами проектов")
public interface MeetingControllerApi {

    @Operation(summary = "Получить все встречи с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список встреч получен")
    })
    @GetMapping
    ResponseEntity<Page<MeetingResponseDto>> getAllMeetings(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы", example = "3")
            @RequestParam(defaultValue = "3") int size,

            @Parameter(description = "Поле для сортировки", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Направление сортировки (true - по возрастанию, false - по убыванию)", example = "true")
            @RequestParam(defaultValue = "true") boolean ascending
    );

    @Operation(summary = "Получить встречу по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Встреча найдена"),
            @ApiResponse(responseCode = "404", description = "Встреча не найдена")
    })
    @GetMapping("/{id}")
    ResponseEntity<MeetingResponseDto> getMeetingById(
            @Parameter(description = "ID встречи", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Получить все встречи по ID проекта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список встреч получен"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @GetMapping("/byProject/{projectId}")
    ResponseEntity<List<MeetingResponseDto>> getMeetingsByProjectId(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId
    );

    @Operation(summary = "Создать новую встречу для проекта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Встреча успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @PostMapping("/project/{projectId}")
    ResponseEntity<MeetingResponseDto> createMeeting(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId,

            @Parameter(description = "Данные встречи", required = true)
            @Valid @RequestBody MeetingRequestDto request
    );

    @Operation(summary = "Обновить существующую встречу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Встреча обновлена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Встреча не найдена")
    })
    @PutMapping("/{id}")
    ResponseEntity<MeetingResponseDto> updateMeeting(
            @Parameter(description = "ID встречи", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные встречи", required = true)
            @Valid @RequestBody MeetingRequestDto request
    );

    @Operation(summary = "Удалить встречу по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Встреча успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Встреча не найдена")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteMeeting(
            @Parameter(description = "ID встречи", required = true, example = "1")
            @PathVariable Long id
    );
}