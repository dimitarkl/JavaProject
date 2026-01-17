package com.example.demo.subject.service;

import com.example.demo.auth.Role;
import com.example.demo.subject.dto.SubjectRequest;
import com.example.demo.subject.model.Subject;
import com.example.demo.subject.repository.SubjectRepository;
import com.example.demo.teacher.model.Teacher;
import com.example.demo.teacher.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SubjectService Integration Tests")
class SubjectServiceIntegrationTest {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        subjectRepository.deleteAll();
        teacherRepository.deleteAll();

        // 1. Първо трябва да имаме учител в базата
        teacher = Teacher.builder()
                .firstName("Petar")
                .lastName("Petrov")
                .email("petar@university.com")
                .password("securePass")
                .role(Role.TEACHER)
                .build();

        teacherRepository.save(teacher);
    }

    @Test
    @DisplayName("Should create subject and persist in database")
    void testCreateSubject_Persistence() {
        SubjectRequest request = new SubjectRequest("Physics", 25, teacher.getId());

        var response = subjectService.createSubject(request);

        assertNotNull(response.id());
        assertEquals("Physics", response.name());
        assertEquals("Petar Petrov", response.teacherName());

        // Проверка директно в базата
        assertTrue(subjectRepository.findById(response.id()).isPresent());
    }

    @Test
    @DisplayName("Should retrieve subject from database")
    void testGetSubject_FromDatabase() {
        Subject subject = Subject.builder()
                .name("History")
                .maxAttendance(100)
                .teacher(teacher)
                .build();

        subjectRepository.save(subject);

        var response = subjectService.getSubjectById(subject.getId());

        assertEquals("History", response.name());
        assertEquals(100, response.maxAttendance());
    }
}