package com.example.demo.subject.service;

import com.example.demo.subject.dto.SubjectRequest;
import com.example.demo.subject.dto.SubjectResponse;
import com.example.demo.subject.model.Subject;
import com.example.demo.subject.repository.SubjectRepository;
import com.example.demo.teacher.model.Teacher;
import com.example.demo.teacher.service.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubjectService Unit Tests")
class SubjectServiceUnitTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private SubjectService subjectService;

    private UUID subjectId;
    private UUID teacherId;
    private Subject subject;
    private Teacher teacher;
    private SubjectRequest subjectRequest;

    @BeforeEach
    void setUp() {
        subjectId = UUID.randomUUID();
        teacherId = UUID.randomUUID();

        teacher = Teacher.builder()
                .id(teacherId)
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan@test.com")
                .build();

        subject = Subject.builder()
                .id(subjectId)
                .name("Mathematics")
                .maxAttendance(30)
                .teacher(teacher)
                .build();

        subjectRequest = new SubjectRequest("Mathematics", 30, teacherId);
    }

    @Test
    @DisplayName("Should create subject successfully")
    void testCreateSubject_Success() {
        when(teacherService.getTeacherEntityById(teacherId)).thenReturn(teacher);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        SubjectResponse result = subjectService.createSubject(subjectRequest);

        assertNotNull(result);
        assertEquals("Mathematics", result.name());
        assertEquals("Ivan Ivanov", result.teacherName());
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw exception when teacher not found")
    void testCreateSubject_TeacherNotFound() {
        when(teacherService.getTeacherEntityById(teacherId))
                .thenThrow(new EntityNotFoundException("Teacher not found with id: " + teacherId));

        assertThrows(EntityNotFoundException.class,
                () -> subjectService.createSubject(subjectRequest));

        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should retrieve subject by ID")
    void testGetSubject_Success() {
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        SubjectResponse result = subjectService.getSubjectById(subjectId);

        assertNotNull(result);
        assertEquals("Mathematics", result.name());
        assertEquals(30, result.maxAttendance());
    }
}