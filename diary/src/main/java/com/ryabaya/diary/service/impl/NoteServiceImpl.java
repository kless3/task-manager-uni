package com.ryabaya.diary.service.impl;

import com.ryabaya.diary.dto.NoteResponseDto;
import com.ryabaya.diary.mapper.NoteMapper;
import com.ryabaya.diary.repository.InMemoryNoteRepository;
import com.ryabaya.diary.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final InMemoryNoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Override
    public NoteResponseDto getNoteById(Long id) {
        return noteMapper.toDto(noteRepository.findById(id));
    }

    @Override
    public NoteResponseDto getNoteByName(String name) {
        return noteMapper.toDto(noteRepository.findByName(name));
    }
}
