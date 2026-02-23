package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.cache.CacheKey;
import com.yafimchyk.labs.cache.CacheManager;
import com.yafimchyk.labs.dto.request.LabelRequestDto;
import com.yafimchyk.labs.dto.response.LabelResponseDto;
import com.yafimchyk.labs.exception.DuplicateResourceException;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.LabelMapper;
import com.yafimchyk.labs.model.Label;
import com.yafimchyk.labs.model.Task;
import com.yafimchyk.labs.repository.LabelRepository;
import com.yafimchyk.labs.service.TaskEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LabelServiceImplTest {

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private LabelMapper labelMapper;

    @Mock
    private TaskEntityService taskEntityService;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private LabelServiceImpl labelService;

    private Task task;
    private Label label;
    private LabelRequestDto labelRequestDto;
    private LabelResponseDto labelResponseDto;
    private final Long labelId = 1L;
    private final Long taskId = 1L;
    private final String labelTitle = "Test Label";

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(taskId);

        label = new Label();
        label.setId(labelId);
        label.setTitle(labelTitle);

        labelRequestDto = new LabelRequestDto(labelTitle);
        labelResponseDto = new LabelResponseDto(labelId, labelTitle);
    }

    @Test
    void getAllLabels_Success() {
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
        when(labelRepository.findAll()).thenReturn(List.of(label));
        when(labelMapper.toDto(label)).thenReturn(labelResponseDto);

        List<LabelResponseDto> result = labelService.getAllLabels();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(labelId);
        verify(cacheManager, times(1)).computeIfAbsent(any(), any());
    }

    @Test
    void getLabelById_Success() {
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));
        when(labelMapper.toDto(label)).thenReturn(labelResponseDto);

        LabelResponseDto result = labelService.getLabelById(labelId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(labelId);
        verify(cacheManager, times(1)).computeIfAbsent(any(), any());
    }

    @Test
    void getLabelById_NotFound_ThrowsException() {
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
        when(labelRepository.findById(labelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> labelService.getLabelById(labelId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Label not found with id: " + labelId);
    }

    @Test
    void createLabel_Success() {
        when(taskEntityService.getTaskEntityById(taskId)).thenReturn(task);
        when(labelRepository.existsByTitle(labelTitle)).thenReturn(false);
        when(labelMapper.toEntity(labelRequestDto)).thenReturn(label);
        when(labelRepository.save(any(Label.class))).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(labelResponseDto);
        doNothing().when(cacheManager).invalidate(Label.class, Task.class);

        LabelResponseDto result = labelService.createLabel(taskId, labelRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(labelId);
        assertThat(result.title()).isEqualTo(labelTitle);
        verify(labelRepository, times(1)).save(label);
        verify(cacheManager, times(1)).invalidate(Label.class, Task.class);
    }

    @Test
    void createLabel_DuplicateTitle_ThrowsException() {
        when(labelRepository.existsByTitle(labelTitle)).thenReturn(true);

        assertThatThrownBy(() -> labelService.createLabel(taskId, labelRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Label already exists!");

        verify(labelRepository, never()).save(any(Label.class));
    }

    @Test
    void updateLabel_Success() {
        final LabelRequestDto updateRequest = new LabelRequestDto("Updated Label");

        Label updatedLabel = new Label();
        updatedLabel.setId(labelId);
        updatedLabel.setTitle("Updated Label");

        LabelResponseDto updatedResponse = new LabelResponseDto(labelId, "Updated Label");

        when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));
        when(labelRepository.existsByTitle(updateRequest.title())).thenReturn(false);
        doNothing().when(labelMapper).updateLabelFromDto(updateRequest, label);
        when(labelRepository.save(any(Label.class))).thenReturn(updatedLabel);
        when(labelMapper.toDto(updatedLabel)).thenReturn(updatedResponse);
        doNothing().when(cacheManager).invalidate(Label.class);

        LabelResponseDto result = labelService.updateLabel(labelId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Updated Label");
        verify(cacheManager, times(1)).invalidate(Label.class);
    }

    @Test
    void updateLabel_DuplicateTitle_ThrowsException() {
        final LabelRequestDto updateRequest = new LabelRequestDto("Duplicate Label");

        when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));
        when(labelRepository.existsByTitle(updateRequest.title())).thenReturn(true);

        assertThatThrownBy(() -> labelService.updateLabel(labelId, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Label already exists!");
    }

    @Test
    void deleteLabel_Success() {
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));
        doNothing().when(labelRepository).delete(label);
        doNothing().when(cacheManager).invalidate(Label.class, Task.class);

        labelService.deleteLabel(labelId);

        verify(labelRepository, times(1)).delete(label);
        verify(cacheManager, times(1)).invalidate(Label.class, Task.class);
    }

    @Test
    void deleteLabel_NotFound_ThrowsException() {
        when(labelRepository.findById(labelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> labelService.deleteLabel(labelId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Label not found with id: " + labelId);
    }

    @Test
    void createLabelEntity_Success() {
        when(labelRepository.existsByTitle(labelTitle)).thenReturn(false);
        when(labelRepository.save(any(Label.class))).thenReturn(label);
        doNothing().when(cacheManager).invalidate(Label.class);

        Label result = labelService.createLabelEntity(labelTitle);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(labelId);
        assertThat(result.getTitle()).isEqualTo(labelTitle);
        verify(labelRepository, times(1)).save(any(Label.class));
        verify(cacheManager, times(1)).invalidate(Label.class);
    }

    @Test
    void createLabelEntity_DuplicateTitle_ThrowsException() {
        when(labelRepository.existsByTitle(labelTitle)).thenReturn(true);

        assertThatThrownBy(() -> labelService.createLabelEntity(labelTitle))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Label already exists!");

        verify(labelRepository, never()).save(any(Label.class));
    }
}