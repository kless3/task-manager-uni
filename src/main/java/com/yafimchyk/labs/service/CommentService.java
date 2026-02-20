package com.yafimchyk.labs.service;

import com.yafimchyk.labs.dto.request.CommentRequestDto;
import com.yafimchyk.labs.dto.response.CommentResponseDto;
import com.yafimchyk.labs.model.Comment;

import java.util.List;

public interface CommentService {

    CommentResponseDto createComment(Long taskId, CommentRequestDto request);

    List<CommentResponseDto> getAllCommentsByTaskId(Long id);

    CommentResponseDto getCommentById(Long id);

    CommentResponseDto updateComment(Long id, CommentRequestDto request);

    void deleteComment(Long id);

    Comment createCommentEntity(Long taskId, String content);

}
