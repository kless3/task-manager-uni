package com.yafimchyk.labs.controller.api;

import com.yafimchyk.labs.dto.request.ProjectRequestDto;
import com.yafimchyk.labs.dto.response.ProjectResponseDto;
import com.yafimchyk.labs.model.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Project Controller", description = "Управление проектами")
public interface ProjectControllerApi {

    @Operation(summary = "Получить все проекты",
            description = "Возвращает список всех проектов с их задачами и встречами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен",
                    content = @Content(schema = @Schema(implementation = ProjectResponseDto.class)))
    })
    @GetMapping
    ResponseEntity<List<ProjectResponseDto>> getAllProjects();

    @Operation(summary = "Получить проект по ID",
            description = "Возвращает детальную информацию о проекте по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект найден",
                    content = @Content(schema = @Schema(implementation = ProjectResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @GetMapping("/{id}")
    ResponseEntity<ProjectResponseDto> getProjectById(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Получить проекты по статусу",
            description = "Возвращает список проектов с указанным статусом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен",
                    content = @Content(schema = @Schema(implementation = ProjectResponseDto.class)))
    })
    @GetMapping("/find")
    ResponseEntity<List<ProjectResponseDto>> getProjectsByStatus(
            @Parameter(description = "Статус проекта", required = true, example = "ACTIVE")
            @RequestParam ProjectStatus status
    );

    @Operation(summary = "Создать новый проект",
            description = "Создает новый проект с указанными параметрами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Проект успешно создан",
                    content = @Content(schema = @Schema(implementation = ProjectResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (ошибка валидации)")
    })
    @PostMapping
    ResponseEntity<ProjectResponseDto> createProject(
            @Parameter(description = "Данные для создания проекта", required = true)
            @Valid @RequestBody ProjectRequestDto request
    );

    @Operation(summary = "Обновить существующий проект",
            description = "Полностью обновляет данные проекта по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект успешно обновлен",
                    content = @Content(schema = @Schema(implementation = ProjectResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (ошибка валидации)"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @PutMapping("/{id}")
    ResponseEntity<ProjectResponseDto> updateProject(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные проекта", required = true)
            @Valid @RequestBody ProjectRequestDto request
    );

    @Operation(summary = "Удалить проект по ID",
            description = "Безвозвратно удаляет проект и все связанные с ним задачи, встречи, комментарии и метки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Проект успешно удален"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProject(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Сложный поиск проектов (JPQL)",
            description = "Поиск проектов по статусу, диапазону дат и метке задачи с использованием JPQL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен",
                    content = @Content(schema = @Schema(implementation = ProjectResponseDto.class)))
    })
    @GetMapping("/search/complex/jpql")
    ResponseEntity<List<ProjectResponseDto>> searchProjectsComplexJpql(
            @Parameter(description = "Статус проекта", required = true, example = "ACTIVE")
            @RequestParam ProjectStatus status,

            @Parameter(description = "Начальная дата дедлайна", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "Конечная дата дедлайна", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Название метки задачи", required = true, example = "BUG")
            @RequestParam String labelTitle
    );

    @Operation(summary = "Сложный поиск проектов (Native SQL)",
            description = "Поиск проектов по статусу, диапазону дат и метке задачи с использованием Native SQL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен",
                    content = @Content(schema = @Schema(implementation = ProjectResponseDto.class)))
    })
    @GetMapping("/search/complex/native")
    ResponseEntity<List<ProjectResponseDto>> searchProjectsComplexNative(
            @Parameter(description = "Статус проекта", required = true, example = "ACTIVE")
            @RequestParam ProjectStatus status,

            @Parameter(description = "Начальная дата дедлайна", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "Конечная дата дедлайна", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Название метки задачи", required = true, example = "BUG")
            @RequestParam String labelTitle
    );
}