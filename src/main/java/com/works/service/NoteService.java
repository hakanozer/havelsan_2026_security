package com.works.service;

import com.works.entity.Note;
import com.works.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public List<Note> list() {
        return noteRepository.findAll();
    }

}
