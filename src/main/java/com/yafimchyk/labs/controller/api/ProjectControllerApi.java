package com.yafimchyk.labs.controller.api;

import com.yafimchyk.labs.dto.request.ProjectRequestDto;
import com.yafimchyk.labs.dto.response.ProjectResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;

@Tag(name = "Project Controller", description = "Управление проектами")
public interface ProjectControllerApi {

    @Operation(summary = "Получить все проекты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен")
    })
    @GetMapping
    ResponseEntity<List<ProjectResponseDto>> getAllProjects();

    @Operation(summary = "Получить проект по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект найден"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @GetMapping("/{id}")
    ResponseEntity<ProjectResponseDto> getProjectById(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Создать новый проект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Проект успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
    @PostMapping
    ResponseEntity<ProjectResponseDto> createProject(
            @Parameter(description = "Данные для создания проекта", required = true)
            @Valid @RequestBody ProjectRequestDto request
    );

    @Operation(summary = "Обновить существующий проект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @PutMapping("/{id}")
    ResponseEntity<ProjectResponseDto> updateProject(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDto request
    );

    @Operation(summary = "Удалить проект по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Проект успешно удален"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProject(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long id
    );
}