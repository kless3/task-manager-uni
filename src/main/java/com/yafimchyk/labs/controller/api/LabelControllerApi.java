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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Label Controller", description = "Управление метками для задач")
public interface LabelControllerApi {

    @Operation(summary = "Получить все метки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список меток получен")
    })
    @GetMapping
    ResponseEntity<List<LabelResponseDto>> getAllLabels();

    @Operation(summary = "Получить метку по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Метка найдена"),
            @ApiResponse(responseCode = "404", description = "Метка не найдена")
    })
    @GetMapping("/{id}")
    ResponseEntity<LabelResponseDto> getLabelById(
            @Parameter(description = "ID метки", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Создать новую метку для задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Метка успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PostMapping("/task/{taskId}")
    ResponseEntity<LabelResponseDto> createLabel(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long taskId,

            @Parameter(description = "Данные метки", required = true)
            @Valid @RequestBody LabelRequestDto request
    );

    @Operation(summary = "Обновить существующую метку")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Метка обновлена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Метка не найдена")
    })
    @PutMapping("/{id}")
    ResponseEntity<LabelResponseDto> updateLabel(
            @Parameter(description = "ID метки", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные метки", required = true)
            @Valid @RequestBody LabelRequestDto request
    );

    @Operation(summary = "Удалить метку по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Метка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Метка не найдена")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteLabel(
            @Parameter(description = "ID метки", required = true, example = "1")
            @PathVariable Long id
    );
}