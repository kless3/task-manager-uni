package com.ryabaya.diary.mapper;

import com.ryabaya.diary.dto.NoteResponseDto;
import com.ryabaya.diary.model.Note;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    NoteResponseDto toDto(Note note);
}
