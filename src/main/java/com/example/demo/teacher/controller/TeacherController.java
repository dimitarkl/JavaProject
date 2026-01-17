package com.example.demo.teacher.controller;

import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.teacher.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @Deprecated
    public TeacherResponse createTeacher(@RequestBody TeacherRequest request) {
        return teacherService.createTeacher(request);
    }

    @GetMapping("/{id}")
    public TeacherResponse getTeacherById(@PathVariable UUID id) {
        return teacherService.getTeacherById(id);
    }

    @GetMapping()
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        return  ResponseEntity.ok(teacherService.getAllTeachers());
    }

}