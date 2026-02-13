package com.ryabaya.diary.controller;

import com.ryabaya.diary.dto.NoteResponseDto;
import com.ryabaya.diary.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDto> getNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @GetMapping("/name")
    public ResponseEntity<NoteResponseDto> getNoteByName(@RequestParam String name) {
        return ResponseEntity.ok(noteService.getNoteByName(name));
    }
}
