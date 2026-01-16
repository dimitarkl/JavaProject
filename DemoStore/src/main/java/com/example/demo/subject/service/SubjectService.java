/*package com.example.demo.subject.service;

import com.example.demo.subject.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
}
*/
/*
package com.example.demo.subject.service;

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

    public Subject createSubject(String name, Integer maxAttendance, UUID teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found!"));

        Subject subject = Subject.builder()
                .name(name)
                .maxAttendance(maxAttendance)
                .teacher(teacher)
                .build();

        return subjectRepository.save(subject);
    }
}
*/
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
                .orElseThrow(() -> new RuntimeException("Teacher not found!"));

        Subject subject = Subject.builder()
                .name(request.name())
                .maxAttendance(request.maxAttendance())
                .teacher(teacher)
                .build();

        Subject saved = subjectRepository.save(subject);
        return mapToResponse(saved);
    }

    // Добавяме метод за взимане по ID (за пълнота)
    public SubjectResponse getSubjectById(UUID id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        return mapToResponse(subject);
    }

    private SubjectResponse mapToResponse(Subject subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getName(),
                subject.getMaxAttendance(),
                subject.getTeacher().getFirstName() + " " + subject.getTeacher().getLastName()
        );
    }
}