package com.example.demo.faculty.controller;

import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RegisterStudentRequest;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.dto.FacultyRequest;
import com.example.demo.faculty.dto.FacultyResponse;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import com.example.demo.student.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("FacultyController API Tests")
class FacultyControllerApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    private String baseUrl;
    private Faculty faculty;
    private String token;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        facultyRepository.deleteAll();

        baseUrl = "/api/faculties";

        faculty = facultyRepository.save(
                Faculty.builder()
                        .name("Test Faculty")
                        .email("faculty@test.com")
                        .phone("123456789")
                        .build()
        );

        token = registerAndLoginStudent();
    }

    private String registerAndLoginStudent() {

        Course tempCourse = courseRepository.save(
                Course.builder()
                        .name("Temp course")
                        .faculty(faculty)
                        .build()
        );


        RegisterStudentRequest register = new RegisterStudentRequest();
        register.setEmail("student@test.com");
        register.setPassword("password123");
        register.setFirstName("Test");
        register.setLastName("Student");
        register.setCourseId(tempCourse.getId());

        String registerUrl = restTemplate.getRootUri() + "/api/auth/register/student";
        restTemplate.postForEntity(registerUrl, register, String.class);


        LoginRequest login = new LoginRequest();
        login.setEmail("student@test.com");
        login.setPassword("password123");

        String loginUrl = restTemplate.getRootUri() + "/api/auth/login";
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, login, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(response.getBody(), Map.class);
            return map.get("accessToken");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse login response JSON", e);
        }
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @DisplayName("Create Faculty")
    void testCreateFaculty() {
        FacultyRequest request = new FacultyRequest(
                "Engineering",
                "engineering@university.com",
                "+359888123456"
        );

        ResponseEntity<FacultyResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request, authHeaders()),
                FacultyResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Engineering", response.getBody().name());
    }

    @Test
    @DisplayName("Get Faculty by ID")
    void testGetFacultyById() {
        ResponseEntity<FacultyResponse> response = restTemplate.exchange(
                baseUrl + "/" + faculty.getId(),
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                FacultyResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Faculty", response.getBody().name());
    }

    @Test
    @DisplayName("Get All Faculties")
    void testGetAllFaculties() {
        facultyRepository.save(
                Faculty.builder()
                        .name("Science")
                        .email("science@uni.com")
                        .phone("88888888")
                        .build()
        );

        ResponseEntity<List<FacultyResponse>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() >= 2);
    }

    @Test
    @DisplayName("Update Faculty")
    void testUpdateFaculty() {
        FacultyRequest update = new FacultyRequest(
                "Updated Faculty",
                "updated@uni.com",
                "00000000"
        );

        ResponseEntity<FacultyResponse> response = restTemplate.exchange(
                baseUrl + "/" + faculty.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(update, authHeaders()),
                FacultyResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Faculty", response.getBody().name());
    }

    /*@Test
    @DisplayName("Delete Faculty")
    void testDeleteFaculty() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + faculty.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(authHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(facultyRepository.findById(faculty.getId()).isEmpty());
    }*/
}
