package com.example.demo.course.controller;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RegisterStudentRequest;
import com.example.demo.course.dto.CourseRequest;
import com.example.demo.course.dto.CourseResponse;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import com.example.demo.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("CourseController API Tests")
class CourseControllerApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

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

        baseUrl = "/api/courses";

        faculty = Faculty.builder()
                .name("Engineering")
                .email("engineering@university.com")
                .phone("+359888123456")
                .build();
        facultyRepository.save(faculty);

        token = registerAndLoginStudent();
    }


    private String registerAndLoginStudent() {

        RegisterStudentRequest register = new RegisterStudentRequest();
        register.setEmail("student@uni.com");
        register.setPassword("password123");
        register.setFirstName("Ivan");
        register.setLastName("Ivanov");
        register.setCourseId(null);

        Course tempCourse = courseRepository.save(
                Course.builder().name("Temp course").faculty(faculty).build()
        );

        register.setCourseId(tempCourse.getId());

        restTemplate.postForEntity("/auth/register/student", register, Void.class);

        LoginRequest login = new LoginRequest();
        login.setEmail("student@uni.com");
        login.setPassword("password123");

        ResponseEntity<AuthResponse> response =
                restTemplate.postForEntity("/auth/login", login, AuthResponse.class);

        return "Bearer " + response.getBody().getAccessToken();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.substring(7));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @DisplayName("Create Course")
    void testCreateCourse() {
        CourseRequest request = new CourseRequest("Computer Science", faculty.getId());

        ResponseEntity<CourseResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request, authHeaders()),
                CourseResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Computer Science", response.getBody().name());
    }

    @Test
    @DisplayName("Get Course by ID")
    void testGetCourseById() {
        Course saved = courseRepository.save(
                Course.builder().name("Computer Science").faculty(faculty).build()
        );

        ResponseEntity<CourseResponse> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                CourseResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Computer Science", response.getBody().name());
    }

    @Test
    @DisplayName("Get Courses by Faculty")
    void testGetCoursesByFaculty() {
        courseRepository.saveAll(List.of(
                Course.builder().name("Computer Science").faculty(faculty).build(),
                Course.builder().name("Electrical Engineering").faculty(faculty).build()
        ));

        ResponseEntity<List<CourseResponse>> response = restTemplate.exchange(
                baseUrl + "/faculty/" + faculty.getId(),
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() >= 2);
    }

    @Test
    @DisplayName("Update Course")
    void testUpdateCourse() {
        Course saved = courseRepository.save(
                Course.builder().name("Computer Science").faculty(faculty).build()
        );

        CourseRequest updateRequest = new CourseRequest("Computer Engineering", faculty.getId());

        ResponseEntity<CourseResponse> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest, authHeaders()),
                CourseResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Computer Engineering", response.getBody().name());
    }

    @Test
    @DisplayName("Delete Course")
    void testDeleteCourse() {
        Course saved = courseRepository.save(
                Course.builder().name("Computer Science").faculty(faculty).build()
        );

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(authHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(courseRepository.findById(saved.getId()).isEmpty());
    }
}
