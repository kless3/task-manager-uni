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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Project Controller", description = "Управление проектами")
public interface ProjectControllerApi {

    @Operation(summary = "Получить все проекты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список проектов получен")
    })
    @GetMapping
    ResponseEntity<List<ProjectResponseDto>> getAllProjects();

    @Operation(summary = "Получить проект по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект найден"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @GetMapping("/{id}")
    ResponseEntity<ProjectResponseDto> getProjectById(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Создать новый проект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Проект успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    @PostMapping
    ResponseEntity<ProjectResponseDto> createProject(
            @Parameter(description = "Данные проекта", required = true)
            @Valid @RequestBody ProjectRequestDto request
    );

    @Operation(summary = "Обновить существующий проект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @PutMapping("/{id}")
    ResponseEntity<ProjectResponseDto> updateProject(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные проекта", required = true)
            @Valid @RequestBody ProjectRequestDto request
    );

    @Operation(summary = "Удалить проект по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Проект успешно удален"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProject(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Фильтрация проектов (JPQL)",
            description = "Получить отфильтрованные проекты по статусу, меткам и сроку")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список отфильтрованных проектов получен")
    })
    @GetMapping("/filter")
    ResponseEntity<List<ProjectResponseDto>> getFilteredProjects(
            @Parameter(description = "Статус проекта", required = true,
                    schema = @Schema(implementation = ProjectStatus.class), example = "IN_PROGRESS")
            @RequestParam ProjectStatus status,

            @Parameter(description = "Названия меток для фильтрации",
                    array = @ArraySchema(schema = @Schema(implementation = String.class)))
            @RequestParam Set<String> labelTitles,

            @Parameter(description = "Включать просроченные проекты", example = "false")
            @RequestParam(defaultValue = "false") boolean includedExpired
    );

    @Operation(summary = "Фильтрация проектов (Native SQL)",
            description = "Получить отфильтрованные проекты используя нативный SQL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список отфильтрованных проектов получен")
    })
    @GetMapping("/filter/native")
    ResponseEntity<List<ProjectResponseDto>> getFilteredProjectsNative(
            @Parameter(description = "Статус проекта", required = true,
                    schema = @Schema(implementation = ProjectStatus.class), example = "IN_PROGRESS")
            @RequestParam ProjectStatus status,

            @Parameter(description = "Названия меток для фильтрации",
                    array = @ArraySchema(schema = @Schema(implementation = String.class)))
            @RequestParam Set<String> labelTitles,

            @Parameter(description = "Включать просроченные проекты", example = "false")
            @RequestParam(defaultValue = "false") boolean includedExpired
    );
}