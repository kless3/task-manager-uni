package com.yafimchyk.labs.controller.api;

import com.yafimchyk.labs.dto.request.MeetingBulkRequestDto;
import com.yafimchyk.labs.dto.request.MeetingRequestDto;
import com.yafimchyk.labs.dto.response.MeetingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Meeting Controller", description = "Управление встречами проектов")
public interface MeetingControllerApi {

    @Operation(summary = "Получить все встречи с пагинацией",
            description = "Возвращает страницу со встречами. Поддерживает сортировку по любому полю.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список встреч успешно получен",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    ResponseEntity<Page<MeetingResponseDto>> getAllMeetings(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы", example = "3")
            @RequestParam(defaultValue = "3") int size,

            @Parameter(description = "Поле для сортировки", example = "meetingDate")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Направление сортировки (true - по возрастанию, false - по убыванию)",
                    example = "true")
            @RequestParam(defaultValue = "true") boolean ascending
    );

    @Operation(summary = "Получить встречу по ID",
            description = "Возвращает детальную информацию о встрече по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Встреча найдена",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Встреча с указанным ID не найдена")
    })
    @GetMapping("/{id}")
    ResponseEntity<MeetingResponseDto> getMeetingById(
            @Parameter(description = "ID встречи", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Получить все встречи по ID проекта",
            description = "Возвращает список всех встреч, принадлежащих указанному проекту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список встреч успешно получен",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @GetMapping("/byProject/{projectId}")
    ResponseEntity<List<MeetingResponseDto>> getMeetingsByProjectId(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId
    );

    @Operation(summary = "Создать новую встречу для проекта",
            description = "Создает встречу с указанными параметрами в существующем проекте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Встреча успешно создана",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (ошибка валидации)"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден")
    })
    @PostMapping("/project/{projectId}")
    ResponseEntity<MeetingResponseDto> createMeeting(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId,

            @Parameter(description = "Данные для создания встречи", required = true)
            @Valid @RequestBody MeetingRequestDto request
    );

    @Operation(summary = "Обновить существующую встречу",
            description = "Полностью обновляет данные встречи по её ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Встреча успешно обновлена",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (ошибка валидации)"),
            @ApiResponse(responseCode = "404", description = "Встреча с указанным ID не найдена")
    })
    @PutMapping("/{id}")
    ResponseEntity<MeetingResponseDto> updateMeeting(
            @Parameter(description = "ID встречи", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные встречи", required = true)
            @Valid @RequestBody MeetingRequestDto request
    );

    @Operation(summary = "Удалить встречу по ID",
            description = "Безвозвратно удаляет встречу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Встреча успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Встреча с указанным ID не найдена")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteMeeting(
            @Parameter(description = "ID встречи", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Массовое создание встреч (с транзакцией)",
            description = "Создает несколько встреч для проекта в одной транзакции. "
                    + "При ошибке все изменения откатываются.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Встречи успешно созданы",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка транзакции - все изменения откачены")
    })
    @PostMapping("/project/{projectId}/bulk/withTx")
    ResponseEntity<List<MeetingResponseDto>> bulkCreateMeetingsWithTx(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId,

            @Parameter(description = "Данные для массового создания встреч", required = true)
            @Valid @RequestBody MeetingBulkRequestDto request
    );

    @Operation(summary = "Массовое создание встреч (без транзакции)",
            description = "Создает несколько встреч для проекта без транзакции. "
                    + "При ошибке часть встреч может сохраниться.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Встречи успешно созданы",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "404", description = "Проект с указанным ID не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка - часть встреч может сохраниться")
    })
    @PostMapping("/project/{projectId}/bulk/woTx")
    ResponseEntity<List<MeetingResponseDto>> bulkCreateMeetingsWoTx(
            @Parameter(description = "ID проекта", required = true, example = "1")
            @PathVariable Long projectId,

            @Parameter(description = "Данные для массового создания встреч", required = true)
            @Valid @RequestBody MeetingBulkRequestDto request
    );
}