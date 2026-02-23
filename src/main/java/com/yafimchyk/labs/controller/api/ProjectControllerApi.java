package com.yafimchyk.labs.controller.api;

import com.yafimchyk.labs.dto.request.ProjectRequestDto;
import com.yafimchyk.labs.dto.response.ProjectResponseDto;
import com.yafimchyk.labs.model.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import java.util.Set;

@Tag(name = "Project Controller", description = "Управление проектами")
public interface ProjectControllerApi {

    @Operation(summary = "Получить все проекты",
            description = "Возвращает список всех проектов с их задачами и встречами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен")
    })
    @GetMapping
    ResponseEntity<List<ProjectResponseDto>> getAllProjects();

    @Operation(summary = "Получить проект по ID",
            description = "Возвращает детальную информацию о проекте, включая все задачи и встречи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект найден"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @GetMapping("/{id}")
    ResponseEntity<ProjectResponseDto> getProjectById(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Создать новый проект",
            description = "Создает проект с указанными параметрами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Проект успешно создан"),
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
            @ApiResponse(responseCode = "200", description = "Проект успешно обновлен"),
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
            description = "Безвозвратно удаляет проект и все связанные с ним задачи, метки, комментарии и встречи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Проект успешно удален"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProject(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Фильтрация проектов (JPQL)",
            description = "Получить отфильтрованные проекты по статусу, меткам и сроку. "
                    + "Использует JPQL запрос с JOIN FETCH для оптимизации загрузки данных.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список отфильтрованных проектов успешно получен")
    })
    @GetMapping("/filter")
    ResponseEntity<List<ProjectResponseDto>> getFilteredProjects(
            @Parameter(description = "Статус проекта для фильтрации", required = true,
                    schema = @Schema(implementation = ProjectStatus.class), example = "IN_PROGRESS")
            @RequestParam ProjectStatus status,

            @Parameter(description = "Названия меток для фильтрации (например: BUG, FEATURE, TASK)",
                    array = @ArraySchema(schema = @Schema(implementation = String.class, example = "BUG")))
            @RequestParam Set<String> labelTitles,

            @Parameter(description = "Включать просроченные проекты (deadline < текущей даты)",
                    example = "false")
            @RequestParam(defaultValue = "false") boolean includedExpired
    );

    @Operation(summary = "Фильтрация проектов (Native SQL)",
            description = "Получить отфильтрованные проекты используя нативный SQL запрос. "
                    + "Работает быстрее на больших объемах данных.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список отфильтрованных проектов успешно получен")
    })
    @GetMapping("/filter/native")
    ResponseEntity<List<ProjectResponseDto>> getFilteredProjectsNative(
            @Parameter(description = "Статус проекта для фильтрации", required = true,
                    schema = @Schema(implementation = ProjectStatus.class), example = "IN_PROGRESS")
            @RequestParam ProjectStatus status,

            @Parameter(description = "Названия меток для фильтрации (например: BUG, FEATURE, TASK)",
                    array = @ArraySchema(schema = @Schema(implementation = String.class, example = "BUG")))
            @RequestParam Set<String> labelTitles,

            @Parameter(description = "Включать просроченные проекты (deadline < текущей даты)",
                    example = "false")
            @RequestParam(defaultValue = "false") boolean includedExpired
    );
}