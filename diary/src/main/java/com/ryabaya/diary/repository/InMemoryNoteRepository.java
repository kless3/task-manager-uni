package com.ryabaya.diary.repository;

import com.ryabaya.diary.model.Note;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryNoteRepository {

    private final List<Note> storage = new ArrayList<>();

    public InMemoryNoteRepository() {
        storage.add(new Note(1L, "Встреча", "Встреча с командой в 10:00"));
        storage.add(new Note(2L, "Дедлайн", "Сдать проект до 18:00"));
        storage.add(new Note(3L, "Звонок", "Позвонить клиенту в 15:30"));
        storage.add(new Note(4L, "Обед", "Заказать пиццу"));
        storage.add(new Note(5L, "Спорт", "Тренировка в 19:00"));
        storage.add(new Note(6L, "Документы", "Подготовить отчет"));
        storage.add(new Note(7L, "Покупки", "Купить продукты"));
        storage.add(new Note(8L, "Книга", "Дочитать главу 5"));
        storage.add(new Note(9L, "Почта", "Ответить на письма"));
        storage.add(new Note(10L, "План", "Составить план на завтра"));
    }

    public Note findById(Long id) {
        for (Note note : storage) {
            if (note.getId().equals(id)) {
                return note;
            }
        }
        return null;
    }

    public Note findByName(String name) {
        for (Note note : storage) {
            if (note.getName().equals(name)) {
                return note;
            }
        }
        return null;
    }
}