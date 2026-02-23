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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Comment Controller", description = "Управление комментариями к задачам")
public interface CommentControllerApi {

    @Operation(summary = "Получить комментарий по ID",
            description = "Возвращает детальную информацию о комментарии по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий найден",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Комментарий с указанным ID не найден")
    })
    @GetMapping("/{id}")
    ResponseEntity<CommentResponseDto> getCommentById(
            @Parameter(description = "ID комментария", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Получить все комментарии по ID задачи",
            description = "Возвращает список всех комментариев, принадлежащих указанной задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список комментариев успешно получен"),
            @ApiResponse(responseCode = "404", description = "Задача с указанным ID не найдена")
    })
    @GetMapping("/byTask/{taskId}")
    ResponseEntity<List<CommentResponseDto>> getCommentsByTaskId(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long taskId
    );

    @Operation(summary = "Создать новый комментарий для задачи",
            description = "Создает комментарий и привязывает его к указанной задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Комментарий успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (ошибка валидации)"),
            @ApiResponse(responseCode = "404", description = "Задача с указанным ID не найдена")
    })
    @PostMapping("/task/{taskId}")
    ResponseEntity<CommentResponseDto> createComment(
            @Parameter(description = "ID задачи", required = true, example = "1")
            @PathVariable Long taskId,

            @Parameter(description = "Данные для создания комментария", required = true)
            @Valid @RequestBody CommentRequestDto request
    );

    @Operation(summary = "Обновить существующий комментарий",
            description = "Полностью обновляет данные комментария по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (ошибка валидации)"),
            @ApiResponse(responseCode = "404", description = "Комментарий с указанным ID не найдена")
    })
    @PutMapping("/{id}")
    ResponseEntity<CommentResponseDto> updateComment(
            @Parameter(description = "ID комментария", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные комментария", required = true)
            @Valid @RequestBody CommentRequestDto request
    );

    @Operation(summary = "Удалить комментарий по ID",
            description = "Безвозвратно удаляет комментарий")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Комментарий успешно удален"),
            @ApiResponse(responseCode = "404", description = "Комментарий с указанным ID не найден")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID комментария", required = true, example = "1")
            @PathVariable Long id
    );
}