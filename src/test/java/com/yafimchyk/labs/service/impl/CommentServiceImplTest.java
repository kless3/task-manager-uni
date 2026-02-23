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
import static org.mockito.Mockito.times;
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
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private final Long commentId = 1L;
    private final Long taskId = 1L;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(taskId);

        comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Test comment");
        comment.setTask(task);
        comment.setCreatedDate(LocalDateTime.now());

        commentRequestDto = new CommentRequestDto("Test comment");
        commentResponseDto = new CommentResponseDto(
                commentId,
                "Test comment",
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void createComment_Success() {
        when(taskEntityService.getTaskEntityById(taskId)).thenReturn(task);
        when(commentMapper.toEntity(commentRequestDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentResponseDto);

        CommentResponseDto result = commentService.createComment(taskId, commentRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(commentId);
        assertThat(result.content()).isEqualTo("Test comment");
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void getAllCommentsByTaskId_Success() {
        when(commentRepository.findByTaskId(taskId)).thenReturn(List.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentResponseDto);

        List<CommentResponseDto> result = commentService.getAllCommentsByTaskId(taskId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(commentId);
    }

    @Test
    void getCommentById_Success() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentResponseDto);

        CommentResponseDto result = commentService.getCommentById(commentId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(commentId);
    }

    @Test
    void getCommentById_NotFound_ThrowsException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getCommentById(commentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Comment not found with id: " + commentId);
    }

    @Test
    void updateComment_Success() {
        final CommentRequestDto updateRequest = new CommentRequestDto("Updated comment");
        Comment updatedComment = new Comment();
        updatedComment.setId(commentId);
        updatedComment.setContent("Updated comment");
        CommentResponseDto updatedResponse = new CommentResponseDto(
                commentId,
                "Updated comment",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentMapper).updateCommentFromDto(updateRequest, comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);
        when(commentMapper.toDto(updatedComment)).thenReturn(updatedResponse);

        CommentResponseDto result = commentService.updateComment(commentId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("Updated comment");
        verify(commentMapper).updateCommentFromDto(updateRequest, comment);
    }

    @Test
    void deleteComment_Success() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        commentService.deleteComment(commentId);

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_NotFound_ThrowsException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(commentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Comment not found with id: " + commentId);
    }

    @Test
    void createCommentEntity_Success() {
        String content = "Test comment entity";
        when(taskEntityService.getTaskEntityById(taskId)).thenReturn(task);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0);
            savedComment.setId(2L);
            return savedComment;
        });

        Comment result = commentService.createCommentEntity(taskId, content);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getTask()).isEqualTo(task);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}