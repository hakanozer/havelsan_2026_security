package com.works.controller;

import com.works.entity.Note;
import com.works.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("note")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping("save")
    public Note save(@RequestBody Note note) {
        return noteService.save(note);
    }

    @GetMapping("list")
    public List<Note> list() {
        return noteService.list();
    }

}
