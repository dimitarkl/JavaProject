package com.example.demo.subject.controller;

import com.example.demo.subject.dto.SubjectRequest;
import com.example.demo.subject.dto.SubjectResponse;
import com.example.demo.subject.service.SubjectService; //
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public SubjectResponse createSubject(@RequestBody SubjectRequest request) {
        return subjectService.createSubject(request);
    }

    @GetMapping("/{id}")
    public SubjectResponse getSubjectById(@PathVariable UUID id) {
        return subjectService.getSubjectById(id);
    }

    @GetMapping
    public List<SubjectResponse> getAllSubjects() {
        return subjectService.getAllSubjects();
    }
}