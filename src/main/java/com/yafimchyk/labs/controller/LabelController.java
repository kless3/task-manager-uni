package com.yafimchyk.labs.controller;

import com.yafimchyk.labs.controller.api.LabelControllerApi;
import com.yafimchyk.labs.dto.request.LabelRequestDto;
import com.yafimchyk.labs.dto.response.LabelResponseDto;
import com.yafimchyk.labs.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/labels")
@RequiredArgsConstructor
public class LabelController implements LabelControllerApi {

    private final LabelService labelService;

    @GetMapping
    public ResponseEntity<List<LabelResponseDto>> getAllLabels() {
        return ResponseEntity.ok(labelService.getAllLabels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelResponseDto> getLabelById(@PathVariable Long id) {
        return ResponseEntity.ok(labelService.getLabelById(id));
    }

    @PostMapping("/task/{taskId}")
    public ResponseEntity<LabelResponseDto> createLabel(
            @PathVariable Long taskId,
            @Valid @RequestBody LabelRequestDto request) {
        LabelResponseDto createdLabel = labelService.createLabel(taskId, request);
        return new ResponseEntity<>(createdLabel, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelResponseDto> updateLabel(
            @PathVariable Long id,
            @Valid @RequestBody LabelRequestDto request) {
        return ResponseEntity.ok(labelService.updateLabel(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
        return ResponseEntity.noContent().build();
    }
}