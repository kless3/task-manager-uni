package com.yafimchyk.labs.controller.api;

import com.yafimchyk.labs.dto.request.TaskCreationDto;
import com.yafimchyk.labs.dto.request.TaskRequestDto;
import com.yafimchyk.labs.dto.response.TaskResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task Controller", description = "Управление задачами в проектах")
public interface TaskControllerApi {

    @Operation(summary = "Получить все задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач получен")
    })
    @GetMapping
    ResponseEntity<List<TaskResponseDto>> getAllTasks();

    @Operation(summary = "Получить задачу по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @GetMapping("/{id}")
    ResponseEntity<TaskResponseDto> getTaskById(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Получить задачу по названию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @GetMapping("/byTitle")
    ResponseEntity<TaskResponseDto> getTaskByTitle(
            @Parameter(description = "Название задачи", required = true, example = "Разработка API")
            @RequestParam String title
    );

    @Operation(summary = "Получить все задачи по ID проекта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач получен"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @GetMapping("/byProject/{projectId}")
    ResponseEntity<List<TaskResponseDto>> getTasksByProjectId(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId
    );

    @Operation(summary = "Создать новую задачу в проекте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @PostMapping("/project/{projectId}")
    ResponseEntity<TaskResponseDto> createTask(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId,

            @Parameter(description = "Данные задачи", required = true)
            @Valid @RequestBody TaskRequestDto request
    );

    @Operation(summary = "Обновить существующую задачу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача обновлена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PutMapping("/{id}")
    ResponseEntity<TaskResponseDto> updateTask(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные задачи", required = true)
            @Valid @RequestBody TaskRequestDto request
    );

    @Operation(summary = "Удалить задачу по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Создать задачу с меткой и комментарием без транзакции",
            description = "Специальный метод для демонстрации проблем с транзакциями")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача создана (возможно с проблемами)"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @PostMapping("/project/{projectId}/wotx")
    ResponseEntity<TaskResponseDto> createTaskWoTx(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId,

            @Parameter(description = "Данные для создания задачи с меткой и комментарием", required = true)
            @RequestBody TaskCreationDto request
    );
}