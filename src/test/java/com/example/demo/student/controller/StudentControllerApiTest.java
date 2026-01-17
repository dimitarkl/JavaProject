package com.example.demo.student.controller;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.RegisterStudentRequest;
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
    private String authRegisterUrl;
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
        authRegisterUrl = "http://localhost:" + port + "/auth/register/student";
        authLoginUrl = "http://localhost:" + port + "/auth/login";
    }

    private String registerAndLogin(String email, String password) {

        RegisterStudentRequest registerRequest = new RegisterStudentRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setCourseId(course.getId());

        ResponseEntity<AuthResponse> registerResponse =
                restTemplate.postForEntity(authRegisterUrl, registerRequest, AuthResponse.class);
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        String token = registerResponse.getBody().getAccessToken();
        assertNotNull(token);

        return token;
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
        String token = registerAndLogin("john3@test.com", "123456");

        UUID studentId = studentRepository.findAll().get(0).getId();

        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(token));

        ResponseEntity<Void> deleteResponse =
                restTemplate.exchange(baseUrl + "/" + studentId, HttpMethod.DELETE, entity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        assertTrue(studentRepository.findById(studentId).isEmpty());
    }
}
