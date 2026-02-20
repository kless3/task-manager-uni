package com.yafimchyk.labs.service.impl;

import com.yafimchyk.labs.dto.request.CommentRequestDto;
import com.yafimchyk.labs.dto.response.CommentResponseDto;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import com.yafimchyk.labs.mapper.CommentMapper;
import com.yafimchyk.labs.model.Comment;
import com.yafimchyk.labs.model.Task;
import com.yafimchyk.labs.repository.CommentRepository;
import com.yafimchyk.labs.service.CommentService;
import com.yafimchyk.labs.service.TaskEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String COMMENT_NOT_FOUND = "Comment not found with id: ";

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TaskEntityService taskEntityService;

    @Override
    @Transactional
    public CommentResponseDto createComment(Long taskId, CommentRequestDto request) {

        Task taskEntity = taskEntityService.getTaskEntityById(taskId);
        Comment comment = commentMapper.toEntity(request);

        comment.setTask(taskEntity);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getAllCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDto getCommentById(Long id) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND + id));
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto request) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND + id));

        commentMapper.updateCommentFromDto(request, comment);
        comment.setUpdatedDate(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment targetComment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND + id));
        commentRepository.delete(targetComment);
    }

    @Override
    public Comment createCommentEntity(Long taskId, String content) {
        Task taskEntity = taskEntityService.getTaskEntityById(taskId);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setTask(taskEntity);
        comment.setCreatedDate(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        taskEntity.getComments().add(savedComment);

        return savedComment;
    }
}
