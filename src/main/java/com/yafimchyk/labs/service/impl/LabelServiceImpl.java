package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.LabelRequestDto;
import com.yafimchyk.labs.dto.response.LabelResponseDto;
import com.yafimchyk.labs.exception.DuplicateResourceException;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.LabelMapper;
import com.yafimchyk.labs.model.Label;
import com.yafimchyk.labs.model.Task;
import com.yafimchyk.labs.repository.LabelRepository;
import com.yafimchyk.labs.service.LabelService;
import com.yafimchyk.labs.service.TaskEntityService;
import com.yafimchyk.labs.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private static final String LABEL_NOT_FOUND = "Label not found with id: ";
    private static final String LABEL_ALREADY_EXISTS = "Label already exists!";

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final TaskEntityService taskEntityService;

    @Override
    @Transactional(readOnly = true)
    public List<LabelResponseDto> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LabelResponseDto getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND + id));
        return labelMapper.toDto(label);
    }

    @Override
    @Transactional
    public LabelResponseDto createLabel(Long taskId, LabelRequestDto request) {

        if (labelRepository.existsByTitle(request.title())) {
            throw new DuplicateResourceException(LABEL_ALREADY_EXISTS);
        }

        Task taskEntity = taskEntityService.getTaskEntityById(taskId);
        Label label = labelMapper.toEntity(request);

        taskEntity.getLabels().add(label);
        label.getTasks().add(taskEntity);

        Label savedLabel = labelRepository.save(label);
        return labelMapper.toDto(savedLabel);
    }

    @Override
    @Transactional
    public LabelResponseDto updateLabel(Long id, LabelRequestDto request) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND + id));

        if (!label.getTitle().equals(request.title()) && labelRepository.existsByTitle(request.title())) {
            throw new DuplicateResourceException(LABEL_ALREADY_EXISTS);
        }

        labelMapper.updateLabelFromDto(request, label);
        Label updatedLabel = labelRepository.save(label);
        return labelMapper.toDto(updatedLabel);
    }

    @Override
    @Transactional
    public void deleteLabel(Long id) {
        Label targetLabel = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND + id));

        labelRepository.delete(targetLabel);
    }

    @Override
    public Label createLabelEntity(String title) {
        if (labelRepository.existsByTitle(title)) {
            throw new DuplicateResourceException("Label with title '" + title + "' already exists!");
        }

        Label label = new Label();
        label.setTitle(title);
        return labelRepository.save(label);
    }
}
