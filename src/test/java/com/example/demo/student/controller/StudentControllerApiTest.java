package com.example.demo.student.controller;

import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.model.Student;
import com.example.demo.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Student API Integration Tests")
class StudentControllerApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private Faculty faculty;
    private Course course;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        facultyRepository.deleteAll();

        faculty = Faculty.builder()
                .name("Engineering")
                .email("engineering@example.com")
                .phone("123456789")
                .build();
        facultyRepository.save(faculty);

        course = Course.builder()
                .name("Computer Engineering")
                .faculty(faculty)
                .build();
        courseRepository.save(course);

        baseUrl = "http://localhost:" + port + "/api/students";
    }

    @Test
    @DisplayName("Create Student - Success")
    void testCreateStudent_Success() {
        StudentRequest request = new StudentRequest("John", "Doe", "john@test.com", course.getId());
        ResponseEntity<StudentResponse> response = restTemplate.postForEntity(baseUrl, request, StudentResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().firstName());
        assertTrue(studentRepository.findById(response.getBody().id()).isPresent());
    }

    @Test
    @DisplayName("Create Student - Course Not Found")
    void testCreateStudent_CourseNotFound() {
        StudentRequest request = new StudentRequest("Jane", "Smith", "jane@test.com", UUID.randomUUID());
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Get Student by ID - Success")
    void testGetStudent_Success() {
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .course(course)
                .build();
        studentRepository.save(student);

        ResponseEntity<StudentResponse> response =
                restTemplate.getForEntity(baseUrl + "/" + student.getId(), StudentResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John", response.getBody().firstName());
    }

    @Test
    @DisplayName("Get Student by ID - Not Found")
    void testGetStudent_NotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/" + UUID.randomUUID(), String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Update Student - Success")
    void testUpdateStudent_Success() {
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .course(course)
                .build();
        studentRepository.save(student);

        StudentRequest updateRequest = new StudentRequest("Jane", "Smith", "jane@test.com", course.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<StudentRequest> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<StudentResponse> response = restTemplate.exchange(
                baseUrl + "/" + student.getId(),
                HttpMethod.PUT,
                entity,
                StudentResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Jane", response.getBody().firstName());

        Student updated = studentRepository.findById(student.getId()).orElseThrow();
        assertEquals("Jane", updated.getFirstName());
    }

    @Test
    @DisplayName("Delete Student - Success")
    void testDeleteStudent_Success() {
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .course(course)
                .build();
        studentRepository.save(student);
        UUID studentId = student.getId();

        restTemplate.delete(baseUrl + "/" + studentId);

        assertTrue(studentRepository.findById(studentId).isEmpty());
    }
}
