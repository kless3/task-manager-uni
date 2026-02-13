package com.ryabaya.diary.service;

import com.ryabaya.diary.dto.NoteResponseDto;

public interface NoteService {

    NoteResponseDto getNoteById(Long id);

    NoteResponseDto getNoteByName(String name);

}
