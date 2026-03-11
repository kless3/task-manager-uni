package com.yafimchyk.labs.controller.api;

import com.yafimchyk.labs.dto.request.TaskCreationDto;
import com.yafimchyk.labs.dto.request.TaskRequestDto;
import com.yafimchyk.labs.dto.response.TaskResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Task Controller", description = "Управление задачами в проектах")
public interface TaskControllerApi {

    @Operation(summary = "Получить все задачи",
            description = "Возвращает список всех задач с использованием entity graph для оптимизации запросов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно получен",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class)))
    })
    @GetMapping
    ResponseEntity<List<TaskResponseDto>> getAllTasks();

    @Operation(summary = "Получить все задачи без использования entity graph",
            description = "Возвращает список всех задач стандартным способом (для сравнения производительности)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно получен",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class)))
    })
    @GetMapping("/woGraph")
    ResponseEntity<List<TaskResponseDto>> getAllTasksWoGraph();

    @Operation(summary = "Получить задачу по ID",
            description = "Возвращает детальную информацию о задаче по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Задача с указанным ID не найдена")
    })
    @GetMapping("/{id}")
    ResponseEntity<TaskResponseDto> getTaskById(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Получить задачу по названию",
            description = "Поиск задачи по точному совпадению названия")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Задача с указанным названием не найдена")
    })
    @GetMapping("/byTitle")
    ResponseEntity<TaskResponseDto> getTaskByTitle(
            @Parameter(description = "Название задачи", required = true, example = "Разработка API")
            @RequestParam String title
    );

    @Operation(summary = "Получить все задачи по ID проекта",
            description = "Возвращает список всех задач, принадлежащих указанному проекту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно получен",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @GetMapping("/byProject/{projectId}")
    ResponseEntity<List<TaskResponseDto>> getTasksByProjectId(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId
    );

    @Operation(summary = "Создать новую задачу в проекте",
            description = "Создает задачу с указанными параметрами в существующем проекте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (ошибка валидации)"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @PostMapping("/project/{projectId}")
    ResponseEntity<TaskResponseDto> createTask(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId,

            @Parameter(description = "Данные для создания задачи", required = true)
            @Valid @RequestBody TaskRequestDto request
    );

    @Operation(summary = "Обновить существующую задачу",
            description = "Полностью обновляет данные задачи по её ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (ошибка валидации)"),
            @ApiResponse(responseCode = "404", description = "Задача с указанным ID не найдена")
    })
    @PutMapping("/{id}")
    ResponseEntity<TaskResponseDto> updateTask(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные задачи", required = true)
            @Valid @RequestBody TaskRequestDto request
    );

    @Operation(summary = "Удалить задачу по ID",
            description = "Безвозвратно удаляет задачу и все связанные с ней комментарии и связи с метками")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача с указанным ID не найдена")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Создать задачу с меткой и комментарием (без транзакции)",
            description = "Демонстрационный метод, показывающий проблемы с транзакциями. "
                    + "При initiateProblem = true создаст ошибку после сохранения комментария, "
                    + "но до сохранения задачи - комментарий останется в БД без задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана (если initiateProblem = false)",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка транзакции - комментарий сохранен без задачи")
    })
    @PostMapping("/project/{projectId}/woTx")
    ResponseEntity<TaskResponseDto> createTaskWoTx(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId,

            @Parameter(description = "Данные для создания задачи с меткой и комментарием", required = true)
            @RequestBody TaskCreationDto request
    );

    @Operation(summary = "Создать задачу с меткой и комментарием (с транзакцией)",
            description = "Демонстрационный метод, показывающий правильную работу транзакций. "
                    + "При возникновении ошибки все изменения откатываются")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка транзакции - все изменения откачены")
    })
    @PostMapping("/project/{projectId}/withTx")
    ResponseEntity<TaskResponseDto> createTaskWithTx(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId,

            @Parameter(description = "Данные для создания задачи с меткой и комментарием", required = true)
            @RequestBody TaskCreationDto request
    );
}