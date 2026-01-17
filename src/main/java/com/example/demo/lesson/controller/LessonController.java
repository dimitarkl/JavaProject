package com.example.demo.lesson.controller;


import com.example.demo.lesson.dto.LessonRequest;
import com.example.demo.lesson.dto.LessonResponse;
import com.example.demo.lesson.model.Lesson;
import com.example.demo.lesson.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
// -------------------------------------------------------------

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    public LessonResponse createLesson(@RequestBody LessonRequest request) {
        return lessonService.createLesson(request);
    }

    @GetMapping("/future")
    public List<Lesson> getFutureLessons() {
        return lessonService.getFutureLessons();
    }

    @GetMapping("/{id}")
    public LessonResponse getLessonById(@PathVariable UUID id) {
        return lessonService.getLessonById(id);
    }
}