package com.yafimchyk.labs.service;

import com.yafimchyk.labs.dto.request.LabelRequestDto;
import com.yafimchyk.labs.dto.response.LabelResponseDto;
import com.yafimchyk.labs.model.Label;

import java.util.List;

public interface LabelService {

    List<LabelResponseDto> getAllLabels();

    LabelResponseDto getLabelById(Long id);

    LabelResponseDto createLabel(Long taskId, LabelRequestDto request);

    LabelResponseDto attachLabelToTask(Long labelId, Long taskId);

    LabelResponseDto updateLabel(Long id, LabelRequestDto request);

    void deleteLabel(Long id);

    Label createLabelEntity(String title);
}
