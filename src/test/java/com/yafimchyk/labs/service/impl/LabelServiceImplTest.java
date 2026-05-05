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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
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

    @Captor
    private ArgumentCaptor<CacheKey> cacheKeyCaptor;

    private Task task;
    private Label label;
    private LabelRequestDto requestDto;
    private LabelResponseDto responseDto;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);

        label = new Label();
        label.setId(1L);
        label.setTitle("BUG");

        requestDto = new LabelRequestDto("BUG");

        responseDto = new LabelResponseDto(1L, "BUG");
    }

    @Test
    void getAllLabels_ShouldReturnCachedOrFromDb() {
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any())).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        when(labelRepository.findAll()).thenReturn(List.of(label));
        when(labelMapper.toDto(label)).thenReturn(responseDto);

        var result = labelService.getAllLabels();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().title()).isEqualTo("BUG");

        verify(cacheManager).computeIfAbsent(cacheKeyCaptor.capture(), any());
        CacheKey capturedKey = cacheKeyCaptor.getValue();
        assertThat(capturedKey.entityClass()).isEqualTo(Label.class);
        assertThat(capturedKey.methodName()).isEqualTo("getAllLabels");
    }

    @Test
    void getLabelById_WhenExists_ShouldReturn() {
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any())).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));
        when(labelMapper.toDto(label)).thenReturn(responseDto);

        var result = labelService.getLabelById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("BUG");
    }

    @Test
    void getLabelById_WhenNotExists_ShouldThrow() {
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any())).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        when(labelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> labelService.getLabelById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Label not found with id: 99");
    }

    @Test
    void createLabel_WhenValid_ShouldCreate() {
        when(taskEntityService.getTaskEntityById(1L)).thenReturn(task);
        when(labelRepository.findByTitle("BUG")).thenReturn(Optional.empty());
        when(labelMapper.toEntity(requestDto)).thenReturn(label);
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(responseDto);

        var result = labelService.createLabel(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("BUG");
        assertThat(task.getLabels()).contains(label);
        assertThat(label.getTasks()).contains(task);
        verify(cacheManager).invalidate(Label.class, Task.class);
    }

    @Test
    void createLabel_WhenTitleExists_ShouldAttachExistingLabel() {
        when(taskEntityService.getTaskEntityById(1L)).thenReturn(task);
        when(labelRepository.findByTitle("BUG")).thenReturn(Optional.of(label));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(responseDto);

        var result = labelService.createLabel(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("BUG");
        assertThat(task.getLabels()).contains(label);
        assertThat(label.getTasks()).contains(task);
        verify(labelMapper, org.mockito.Mockito.never()).toEntity(requestDto);
    }

    @Test
    void updateLabel_WhenValid_ShouldUpdate() {
        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));
        doNothing().when(labelMapper).updateLabelFromDto(requestDto, label);
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(responseDto);

        var result = labelService.updateLabel(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("BUG");
        verify(cacheManager).invalidate(Label.class);
        verify(labelMapper).updateLabelFromDto(requestDto, label);
    }

    @Test
    void updateLabel_WhenTitleChangedAndExists_ShouldThrow() {
        Label existingLabel = new Label();
        existingLabel.setId(1L);
        existingLabel.setTitle("OLD");

        when(labelRepository.findById(1L)).thenReturn(Optional.of(existingLabel));
        when(labelRepository.existsByTitle("BUG")).thenReturn(true);

        assertThatThrownBy(() -> labelService.updateLabel(1L, requestDto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void deleteLabel_WhenExists_ShouldDelete() {
        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));
        doNothing().when(labelRepository).delete(label);

        labelService.deleteLabel(1L);

        verify(labelRepository).delete(label);
        verify(cacheManager).invalidate(Label.class, Task.class);
    }

    @Test
    void deleteLabel_WhenNotExists_ShouldThrow() {
        when(labelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> labelService.deleteLabel(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createLabelEntity_WhenTitleNotExists_ShouldCreate() {
        when(labelRepository.existsByTitle("BUG")).thenReturn(false);
        when(labelRepository.save(any(Label.class))).thenReturn(label);

        var result = labelService.createLabelEntity("BUG");

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("BUG");
        verify(cacheManager).invalidate(Label.class);
        verify(labelRepository).save(any(Label.class));
    }

    @Test
    void createLabelEntity_WhenTitleExists_ShouldThrow() {
        when(labelRepository.existsByTitle("BUG")).thenReturn(true);

        assertThatThrownBy(() -> labelService.createLabelEntity("BUG"))
                .isInstanceOf(DuplicateResourceException.class);
    }
}
