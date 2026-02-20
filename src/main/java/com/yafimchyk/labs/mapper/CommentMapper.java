package com.yafimchyk.labs.mapper;

import com.yafimchyk.labs.dto.request.CommentRequestDto;
import com.yafimchyk.labs.dto.response.CommentResponseDto;
import com.yafimchyk.labs.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentResponseDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "task", ignore = true)
    Comment toEntity(CommentRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "task", ignore = true)
    void updateCommentFromDto(CommentRequestDto request, @MappingTarget Comment comment);
}