package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.CommentRequestDto;
import com.yafimchyk.labs.dto.response.CommentResponseDto;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.CommentMapper;
import com.yafimchyk.labs.model.Comment;
import com.yafimchyk.labs.model.Task;
import com.yafimchyk.labs.repository.CommentRepository;
import com.yafimchyk.labs.service.TaskEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private TaskEntityService taskEntityService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Task task;
    private Comment comment;
    private CommentRequestDto requestDto;
    private CommentResponseDto responseDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        task = new Task();
        task.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment content");
        comment.setTask(task);
        comment.setCreatedDate(now);
        comment.setUpdatedDate(now);

        requestDto = new CommentRequestDto("Test comment content");

        responseDto = new CommentResponseDto(
                1L,
                "Test comment content",
                now,
                now
        );
    }

    @Test
    void createComment_ShouldReturnCommentResponseDto() {
        when(taskEntityService.getTaskEntityById(1L)).thenReturn(task);
        when(commentMapper.toEntity(requestDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentResponseDto result = commentService.createComment(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.content()).isEqualTo("Test comment content");
        assertThat(result.createdDate()).isEqualTo(now);
        verify(commentRepository).save(comment);
        verify(commentMapper).toEntity(requestDto);
        verify(commentMapper).toDto(comment);
    }

    @Test
    void getAllCommentsByTaskId_ShouldReturnListOfDtos() {
        when(commentRepository.findByTaskId(1L)).thenReturn(List.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        List<CommentResponseDto> result = commentService.getAllCommentsByTaskId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().content()).isEqualTo("Test comment content");
    }

    @Test
    void getCommentById_WhenExists_ShouldReturnDto() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentResponseDto result = commentService.getCommentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getCommentById_WhenNotExists_ShouldThrow() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getCommentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Comment not found with id: 99");
    }

    @Test
    void updateComment_ShouldUpdateAndReturnDto() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentMapper).updateCommentFromDto(requestDto, comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentResponseDto result = commentService.updateComment(1L, requestDto);

        assertThat(result).isNotNull();
        verify(commentMapper).updateCommentFromDto(requestDto, comment);
        assertThat(comment.getUpdatedDate()).isNotNull();
    }

    @Test
    void deleteComment_WhenExists_ShouldDelete() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        commentService.deleteComment(1L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_WhenNotExists_ShouldThrow() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createCommentEntity_ShouldCreateAndAddToTask() {
        when(taskEntityService.getTaskEntityById(1L)).thenReturn(task);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.createCommentEntity(1L, "Test comment content");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Test comment content");
        assertThat(task.getComments()).contains(comment);
    }
}