package com.example.demo.subject.service;

import com.example.demo.subject.dto.SubjectRequest;
import com.example.demo.subject.dto.SubjectResponse;
import com.example.demo.subject.model.Subject;
import com.example.demo.subject.repository.SubjectRepository;
import com.example.demo.teacher.model.Teacher;
import com.example.demo.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;

    public SubjectResponse createSubject(SubjectRequest request) {
        Teacher teacher = teacherRepository.findById(request.teacherId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        Subject subject = Subject.builder()
                .name(request.name())
                .maxAttendance(request.maxAttendance())
                .teacher(teacher)
                .build();

        Subject savedSubject = subjectRepository.save(subject);
        return mapToResponse(savedSubject);
    }

    public SubjectResponse getSubjectById(UUID id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with id: " + id));

        return mapToResponse(subject);
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
}