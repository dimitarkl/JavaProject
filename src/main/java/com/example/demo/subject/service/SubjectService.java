package com.example.demo.subject.service;

import com.example.demo.subject.dto.SubjectRequest;
import com.example.demo.subject.dto.SubjectResponse;
import com.example.demo.subject.model.Subject;
import com.example.demo.subject.repository.SubjectRepository;
import com.example.demo.teacher.model.Teacher;
import com.example.demo.teacher.repository.TeacherRepository;
import com.example.demo.teacher.service.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TeacherService teacherService;

    public SubjectResponse createSubject(SubjectRequest request) {
        Teacher teacher = teacherService.getTeacherEntityById(request.teacherId());

        Subject subject = Subject.builder()
                .name(request.name())
                .maxAttendance(request.maxAttendance())
                .teacher(teacher)
                .build();

        subjectRepository.save(subject);
        return mapToResponse(subject);
    }

    public SubjectResponse getSubjectById(UUID id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));

        return mapToResponse(subject);
    }

    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SubjectResponse mapToResponse(Subject subject) {
        String fullName = subject.getTeacher().getFirstName() + " " + subject.getTeacher().getLastName();

        return new SubjectResponse(
                subject.getId(),
                subject.getName(),
                subject.getMaxAttendance(),
                fullName
        );
    }

    public Subject getSubjectEntityById(UUID id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));
    }
}