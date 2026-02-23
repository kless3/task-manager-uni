package com.yafimchyk.labs.controller.api;

import com.yafimchyk.labs.dto.request.LabelRequestDto;
import com.yafimchyk.labs.dto.response.LabelResponseDto;
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

@Tag(name = "Label Controller", description = "Управление метками для задач")
public interface LabelControllerApi {

    @Operation(summary = "Получить все метки",
            description = "Возвращает список всех существующих меток в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список меток успешно получен")
    })
    @GetMapping
    ResponseEntity<List<LabelResponseDto>> getAllLabels();

    @Operation(summary = "Получить метку по ID",
            description = "Возвращает информацию о метке по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Метка найдена"),
            @ApiResponse(responseCode = "404", description = "Метка с указанным ID не найдена")
    })
    @GetMapping("/{id}")
    ResponseEntity<LabelResponseDto> getLabelById(
            @Parameter(description = "ID метки", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Создать новую метку для задачи",
            description = "Создает метку и привязывает её к указанной задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Метка успешно создана и привязана к задаче"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (ошибка валидации)"),
            @ApiResponse(responseCode = "404", description = "Задача с указанным ID не найдена")
    })
    @PostMapping("/task/{taskId}")
    ResponseEntity<LabelResponseDto> createLabel(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long taskId,

            @Parameter(description = "Данные для создания метки", required = true)
            @Valid @RequestBody LabelRequestDto request
    );

    @Operation(summary = "Обновить существующую метку",
            description = "Полностью обновляет данные метки по её ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Метка успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (ошибка валидации)"),
            @ApiResponse(responseCode = "404", description = "Метка с указанным ID не найдена")
    })
    @PutMapping("/{id}")
    ResponseEntity<LabelResponseDto> updateLabel(
            @Parameter(description = "ID метки", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные метки", required = true)
            @Valid @RequestBody LabelRequestDto request
    );

    @Operation(summary = "Удалить метку по ID",
            description = "Безвозвратно удаляет метку. Связь с задачами также удаляется.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Метка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Метка с указанным ID не найдена")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteLabel(
            @Parameter(description = "ID метки", required = true, example = "1")
            @PathVariable Long id
    );
}