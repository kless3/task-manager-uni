package com.yafimchyk.labs.controller.api;

import com.yafimchyk.labs.dto.request.CommentRequestDto;
import com.yafimchyk.labs.dto.response.CommentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment Controller", description = "Управление комментариями к задачам")
public interface CommentControllerApi {

    @Operation(summary = "Получить комментарий по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий найден",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    @GetMapping("/{id}")
    ResponseEntity<CommentResponseDto> getCommentById(
            @Parameter(description = "ID комментария", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Получить все комментарии по ID задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список комментариев получен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @GetMapping("/byTask/{taskId}")
    ResponseEntity<List<CommentResponseDto>> getCommentsByTaskId(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long taskId
    );

    @Operation(summary = "Создать новый комментарий для задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Комментарий успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PostMapping("/task/{taskId}")
    ResponseEntity<CommentResponseDto> createComment(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long taskId,

            @Parameter(description = "Данные комментария", required = true)
            @Valid @RequestBody CommentRequestDto request
    );

    @Operation(summary = "Обновить существующий комментарий")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    @PutMapping("/{id}")
    ResponseEntity<CommentResponseDto> updateComment(
            @Parameter(description = "ID комментария", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные комментария", required = true)
            @Valid @RequestBody CommentRequestDto request
    );

    @Operation(summary = "Удалить комментарий по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Комментарий успешно удален"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID комментария", required = true, example = "1")
            @PathVariable Long id
    );
}