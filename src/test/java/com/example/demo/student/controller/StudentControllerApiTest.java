package com.example.demo.student.controller;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.RegisterStudentRequest;
import com.example.demo.auth.dto.RegisterTeacherRequest;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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
    private String authRegisterUrl;
    private String authRegisterTeacherUrl;
    private String authLoginUrl;

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
        authRegisterUrl = "http://localhost:" + port + "/api/auth/register/student";
        authRegisterTeacherUrl = "http://localhost:" + port + "/api/auth/register/teacher";
        authLoginUrl = "http://localhost:" + port + "/api/auth/login";
    }

    private String registerAndLogin(String email, String password) {

        RegisterStudentRequest registerRequest = new RegisterStudentRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setCourseId(course.getId());

        ResponseEntity<String> registerResponse =
                restTemplate.postForEntity(authRegisterUrl, registerRequest, String.class);

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, String> map = mapper.readValue(registerResponse.getBody(), java.util.Map.class);
            String token = map.get("accessToken");
            assertNotNull(token);
            return token;
        } catch (Exception e) {
            fail("Failed to parse AuthResponse JSON: " + e.getMessage());
            return null;
        }
    }

    private String registerAndLoginTeacher(String email, String password) {

        RegisterTeacherRequest registerRequest = new RegisterTeacherRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Doe");

        ResponseEntity<String> registerResponse =
                restTemplate.postForEntity(authRegisterTeacherUrl, registerRequest, String.class);

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, String> map = mapper.readValue(registerResponse.getBody(), java.util.Map.class);
            String token = map.get("accessToken");
            assertNotNull(token);
            return token;
        } catch (Exception e) {
            fail("Failed to parse AuthResponse JSON: " + e.getMessage());
            return null;
        }
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @DisplayName("Create Student")
    void testCreateStudent_Success() {
        String token = registerAndLogin("john@test.com", "123456");

        HttpHeaders headers = authHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<StudentResponse[]> response =
                restTemplate.exchange(baseUrl, HttpMethod.GET, entity, StudentResponse[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("John", response.getBody()[0].firstName());
    }

    @Test
    @DisplayName("Get Student by ID")
    void testGetStudent_Success() {
        String token = registerAndLogin("john2@test.com", "123456");

        UUID studentId = studentRepository.findAll().get(0).getId();

        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(token));

        ResponseEntity<StudentResponse> response =
                restTemplate.exchange(baseUrl + "/" + studentId, HttpMethod.GET, entity, StudentResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John", response.getBody().firstName());
    }

    @Test
    @DisplayName("Delete Student")
    void testDeleteStudent_Success() {
        String studentToken = registerAndLogin("john3@test.com", "123456");
        UUID studentId = studentRepository.findAll().get(0).getId();
        
        String teacherToken = registerAndLoginTeacher("teacher@test.com", "123456");
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(teacherToken));

        ResponseEntity<Void> deleteResponse =
                restTemplate.exchange(baseUrl + "/" + studentId, HttpMethod.DELETE, entity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        assertTrue(studentRepository.findById(studentId).isEmpty());
    }
}